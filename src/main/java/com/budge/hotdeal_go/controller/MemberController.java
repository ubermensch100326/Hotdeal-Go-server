package com.budge.hotdeal_go.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.api.KakaoApi;
import com.budge.hotdeal_go.exception.InvalidTokenFormatException;
import com.budge.hotdeal_go.exception.MemberNotFoundException;
import com.budge.hotdeal_go.exception.TokenExpiredException;
import com.budge.hotdeal_go.model.dto.DeviceDto;
import com.budge.hotdeal_go.model.dto.IdCheckDto;
import com.budge.hotdeal_go.model.dto.LoginDto;
import com.budge.hotdeal_go.model.dto.MemberDto;
import com.budge.hotdeal_go.model.dto.MessageDto;
import com.budge.hotdeal_go.model.dto.RegisterDto;
import com.budge.hotdeal_go.model.dto.TokenDto;
import com.budge.hotdeal_go.model.service.MemberService;
import com.budge.hotdeal_go.util.JWTUtility;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

// 로그인 외에도 /refresh가 구현되어 있어야 함 (액세스 토큰 재발급)
// 로그아웃할 때 백에서 리프레시 토큰을 삭제하고, 프론트에서는 저장된 액세스 토큰과 리프레시 토큰을 삭제해야 함 (클라이언트가 로그아웃 요청을 할 때는 헤더에 액세스 토큰, 바디에 리프레시 토큰 담아서 하게 됨)
// 나중에 유효성 검증 부분도 구현할 것 (@Valid, BindingResult, hasErrors() => new ResponseEntity(HttpStatus.BAD_REQUEST))
// passwordEncoder.match 이용해서 비밀번호 암호화
@RestController
@Validated
@RequestMapping("/member")
@Api("멤버 컨트롤러 API V1")
@Slf4j
public class MemberController {

	private MemberService memberService;
	private JWTUtility jwtUtility;
	private KakaoApi kakaoApi;

	@Autowired
	public MemberController(KakaoApi kakaoApi, MemberService memberService, JWTUtility jwtUtility) {
		super();
		this.kakaoApi = kakaoApi;
		this.memberService = memberService;
		this.jwtUtility = jwtUtility;
	}

	@ApiOperation(value = "[Deprecated] 웹 소셜로그인 (테스트용)", notes = "카카오 인증 (인가) 코드를 이용하여 회원가입 혹은 로그인 처리", response = TokenDto.class)
//	REST API로 구현할 때 바로 액세스 토큰을 받는 것이 아니라 인증 코드를 받아서, 인증 코드를 이용하여 액세스 토큰을 받게 됨
	@GetMapping("/oauth/kakao/test")
	public ResponseEntity<TokenDto> continueWithKakaoTest(
			@RequestParam @ApiParam(value = "카카오 인증 코드", required = true) String code) {
//		카카오로부터 사용자 인증
		log.info("kakao authorization code : {}", code);
		String kakaoAccessToken = kakaoApi.getAccessToken(code);
		log.info("kakao access token : {}", kakaoAccessToken);
		MemberDto memberDto = kakaoApi.getMemberInfo(kakaoAccessToken);
		log.info("kakao oauth member : {}", memberDto);

//		카카오로부터 받은 정보로 사이트 자체적인 회원가입 혹은 로그인
		log.info("findByProviderId - 호출", memberDto);
		MemberDto memberFind = memberService.findByProviderId(memberDto);
		log.info("find member : {}", memberFind);

		if (memberFind == null) {
			memberService.registerMember(memberDto);
			memberFind = memberDto;
		}

//		발급받은 refresh token을 DB에 저장
		String accessToken = jwtUtility
				.createAccessToken(String.valueOf(memberFind.getNo()) + memberFind.getProvider());
		String refreshToken = jwtUtility
				.createRefreshToken(String.valueOf(memberFind.getNo()) + memberFind.getProvider());
		log.info("access token : {}", accessToken);
		log.info("refresh token : {}", refreshToken);
		Map<String, Object> saveMap = new HashMap<>();
		saveMap.put("no", memberFind.getNo());
		saveMap.put("refreshToken", refreshToken);
		memberService.saveRefreshToken(saveMap);

//		JSON으로 token 전달
		TokenDto tokenDto = TokenDto.builder()
				.message("소셜 로그인 완료")
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
		HttpStatus status = HttpStatus.CREATED;

		return new ResponseEntity<TokenDto>(tokenDto, status);
	}

//	Android SDK에서 보내준 액세스 토큰을 이용하여 회원가입, 로그인
//	1. 액세스 토큰으로 로그인
//	- 액세스 토큰으로 kakao resource API에 사용자 정보 요청
//	- 해당 카카오 정보 (provider, provider_id)를 통해 우리 서비스에 가입되어있는지 확인
//		- 가입되어 있지 않으면 로그인 실패
//		- 가입되어 있으면 우리 서비스 자체의 JWT 발급
//	2. 액세스 토큰으로 회원가입
//	- 액세스 토큰으로 kakao reousrce API에 사용자 정보 요청
//	- 해당 카카오 정보 (provider, provider_id)를 통해 우리 서비스에 가입되어있는지 확인
//		- 가입되어 있지 않으면 새로 가입시키고 JWT 발급
//		- 이미 가입되어 있다면 ? 

	@ApiOperation(value = "[Header - kakao access] 안드로이드 소셜로그인", notes = "카카오 액세스 토큰을 이용하여 회원가입 혹은 로그인 처리", response = TokenDto.class)
	@PostMapping("/oauth/kakao")
	public ResponseEntity<TokenDto> continueWithKakao(@RequestBody @ApiParam(value = "기기 고유 ID") DeviceDto deviceDto, HttpServletRequest request) {
		// request 형태 살펴보기
        StringBuilder requestDetails = new StringBuilder();
        requestDetails.append("Request URL: ").append(request.getRequestURL()).append("\n");
        requestDetails.append("Request Method: ").append(request.getMethod()).append("\n");
        requestDetails.append("Request Headers: \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestDetails.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }
        requestDetails.append("Request Parameters: \n");
        request.getParameterMap().forEach((param, values) -> {
            requestDetails.append(param).append(": ");
            for (String value : values) {
                requestDetails.append(value).append(", ");
            }
            requestDetails.append("\n");
        });
        requestDetails.append("Other Request Information: \n");
        requestDetails.append("Remote Host: ").append(request.getRemoteHost()).append("\n");
        requestDetails.append("Content Type: ").append(request.getContentType()).append("\n");
        requestDetails.append("Character Encoding: ").append(request.getCharacterEncoding()).append("\n");
        requestDetails.append("Remote Address: ").append(request.getRemoteAddr());
        System.out.println("======================= Request =======================");
        System.out.println(requestDetails);
        System.out.println("=======================================================");
		
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new InvalidTokenFormatException();
		}
		String kakaoAccessToken = authorization.replace("Bearer ", "");
		
		
		log.info("kakao access token : {}", kakaoAccessToken);
		TokenDto tokenDto = null;
		HttpStatus status = HttpStatus.ACCEPTED;

		try {
//			카카오로부터 사용자 인증
			MemberDto memberDto = kakaoApi.getMemberInfo(kakaoAccessToken);
			log.info("kakao oauth member : {}", memberDto);

//			카카오로부터 받은 정보로 사이트 자체적인 회원가입 혹은 로그인
			log.info("findByProviderId - 호출", memberDto);
			MemberDto memberFind = memberService.findByProviderId(memberDto);
			log.info("find member : {}", memberFind);

//			사이트 자체의 리프레시 토큰이 만료된 상태면 다시 카카오 액세스 토큰을 발급받아 신원을 확인하고 사이트 자체의 리프레시 토큰을 발급해줘야 함
//			기존에 가입된 고객인데 리프레시 토큰이 만료돼서 다시 카카오 로그인으로 계속하는 경우 기존 member 테이블에 데이터 자체는 남아있음
//			그렇기 때문에 아예 신규 고객인 경우, 즉 memberFind가 null일 경우에만 회원 등록하는 과정이 있어야 함
//			이 다음부터는 다시 사이트 자체의 토큰으로 로그인 처리됨 (MemberController)
			int flag = 0;
			if (memberFind == null) {
				memberService.registerMember(memberDto);
				memberFind = memberDto;
			}
			else {
				flag = 1;
			}

			String memberId = String.valueOf(memberFind.getNo()) + memberFind.getProvider();

			String accessToken = jwtUtility.createAccessToken(memberId);
			String refreshToken = jwtUtility.createRefreshToken(memberId);
			log.info("access token : {}", accessToken);
			log.info("refresh token : {}", refreshToken);

//			다른 기기에서 로그인 체크
			if (flag == 1) {
				Map<String, Object> checkMap = new HashMap<>();
				checkMap.put("no", memberFind.getNo());
				checkMap.put("deviceId", deviceDto.getDeviceId());
				if (memberService.checkRefreshToken(checkMap) == null) flag = 0;
			}
			
//			발급받은 refresh token을 DB에 저장
			Map<String, Object> saveMap = new HashMap<>();
			saveMap.put("no", memberFind.getNo());
			saveMap.put("refreshToken", refreshToken);
			saveMap.put("deviceId", deviceDto.getDeviceId());
			saveMap.put("flag", flag);
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			System.out.println(saveMap);
			memberService.saveRefreshToken(saveMap);

//			JSON으로 token 전달
			tokenDto = TokenDto.builder()
					.message("안드로이드 소셜 로그인 완료")
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
			status = HttpStatus.CREATED;

		} catch (Exception e) {
			log.debug("OAuth 처리 중 오류 발생 : {}", e);
			tokenDto = TokenDto.builder()
					.message(e.getMessage())
					.build();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<TokenDto>(tokenDto, status);
	}

	@ApiOperation(value = "사이트 자체 아이디, 비밀번호를 이용한 로그인", notes = "아이디와 비밀번호를 이용하여 로그인 처리", response = TokenDto.class)
	@PostMapping("/login")
	public ResponseEntity<TokenDto> loginMember(
			@RequestBody @ApiParam(value = "로그인 시 필요한 회원정보(아이디, 비밀번호)", required = true) LoginDto loginDto) {
		log.debug("login member : {}", loginDto);
		HttpStatus status = HttpStatus.ACCEPTED;
		TokenDto tokenDto = null;
		try {
			MemberDto memberLogin = memberService.loginMember(loginDto);
			if (memberLogin != null) {
				String memberId = String.valueOf(memberLogin.getNo()) + memberLogin.getProvider();
//				String nickname = memberLogin.getNickname();
//				int admin = memberLogin.getAdmin();
				String accessToken = jwtUtility.createAccessToken(memberId);
				String refreshToken = jwtUtility.createRefreshToken(memberId);
				log.debug("access token : {}", accessToken);
				log.debug("refresh token : {}", refreshToken);

				int flag = 0;
				Map<String, Object> checkMap = new HashMap<>();
				checkMap.put("no", memberLogin.getNo());
				checkMap.put("deviceId", loginDto.getDeviceId());
				if (memberService.checkRefreshToken(checkMap) != null) flag = 1;
				
//				발급받은 refresh token을 DB에 저장
				Map<String, Object> saveMap = new HashMap<>();
				saveMap.put("no", memberLogin.getNo());
				saveMap.put("refreshToken", refreshToken);
				saveMap.put("deviceId", loginDto.getDeviceId());
				saveMap.put("flag", flag);
				memberService.saveRefreshToken(saveMap);

//				JSON으로 token 전달
				tokenDto = TokenDto.builder()
						.message("로그인 완료")
						.accessToken(accessToken)
						.refreshToken(refreshToken)
						.build();
//				tokenMap.put("memberId", memberId);
//				tokenMap.put("nickname", nickname);
//				tokenMap.put("admin", admin);
				status = HttpStatus.CREATED;
			} else {
				tokenDto = TokenDto.builder()
						.message("아이디 또는 패스워드 확인 필요")
						.build();
				status = HttpStatus.UNAUTHORIZED;
			}

		} catch (Exception e) {
			log.debug("로그인 처리 중 오류 발생 : {}", e);
			tokenDto = TokenDto.builder()
					.message(e.getMessage())
					.build();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<TokenDto>(tokenDto, status);
	}

//	계정 통합, 소셜 회원가입과 자체 회원가입 둘 다 하면 어떻게 처리할지 생각해볼 것
//	나중에 REST하게 설계하려면 어떻게 수정해야될지 생각해볼 것
//	일단은 입력되지 않은 정보는 empty string으로 채워지도록 설계함
//	사이트 자체 회원가입은 이후에 별도로 로그인을 해야함 (소셜 로그인은 계속하기를 누르면 자동으로 회원가입 및 로그인 진행)
	@ApiOperation(value = "사이트 자체 회원가입", notes = "회원 정보를 입력하여 회원가입", response = MessageDto.class)
	@PostMapping("/register")
	public ResponseEntity<MessageDto> registerMember(
			@RequestBody @ApiParam(value = "회원 정보 (nickname, id, password, contact_email", required = true) RegisterDto registerDto) {
		log.info("registerMember - {}", registerDto);
		MemberDto memberDto = MemberDto.builder()
				.nickname(registerDto.getNickname())
				.id(registerDto.getId())
				.password(registerDto.getPassword())
				.contactEmail(registerDto.getContactEmail())
				.provider("hotdealgo")
				.build();
		HttpStatus status = HttpStatus.ACCEPTED;
		MessageDto messageDto = null;
		try {
			memberService.registerMember(memberDto);
			messageDto = MessageDto.builder()
					.message("회원가입 완료")
					.build();
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			messageDto = MessageDto.builder()
					.message("회원가입 실패")
					.build();
		}
		return new ResponseEntity<MessageDto>(messageDto, status);
	}

	// @RequestHeader 어떻게 쓰는지 모르겠음 (우선은 HttpServletReqeust 사용)
	@ApiOperation(value = "[Header - refresh] 액세스 토큰 재발급", notes = "리프레시 토큰을 이용하여 액세스 토큰 재발급", response = TokenDto.class)
	@PostMapping("/token")
	public ResponseEntity<TokenDto> reissueAccessToken(@RequestBody @ApiParam(value = "기기 고유 ID") DeviceDto deviceDto, HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new InvalidTokenFormatException();
		}
		String refreshToken = authorization.replace("Bearer ", "");
		int flag = 0;
		HttpStatus status = HttpStatus.ACCEPTED;
//	    토큰에 memberId라는 정보가 있다는 전제 하에 처리하는 방식임
//	    만약 토큰에 memberId라는 정보를 담지 않고 인증 처리를 하고 싶다면 다른 방법을 생각해봐야될 듯
		TokenDto tokenDto = null;
		
		if ((flag = jwtUtility.checkToken(refreshToken, "refresh-token")) == 2) {
			String memberId = jwtUtility.getMemberId(refreshToken);
			int no = Integer.parseInt(memberId.replaceAll("[^0-9]", ""));
			String provider = memberId.replaceAll("[0-9]", "");
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("deviceId", deviceDto.getDeviceId());
			checkMap.put("no", no);
			
//			액세스 토큰 재발급은 로그인에서와 달리 memberId로 회원을 식별하지 않고 refreshToken 자체로 함
			if (refreshToken.equals(memberService.checkRefreshToken(checkMap))) {
				String accessToken = jwtUtility
						.createAccessToken(String.valueOf(no) + provider);
				tokenDto = TokenDto.builder()
						.message("액세스 토큰 재발급 완료")
						.accessToken(accessToken)
						.refreshToken(refreshToken)
						.build();
				status = HttpStatus.CREATED;
			} else {
				log.info("존재하지 않는 리프레시 토큰 : {}", refreshToken);
				// 404 에러
				throw new MemberNotFoundException();
			}
		} else if (flag == 1) {
			log.info("토큰 만료 : {}", refreshToken);
			// 401 에러
			throw new TokenExpiredException();
		} else {
			log.info("토큰 형식 오류 : {}", refreshToken);
			// 401 에러
			throw new InvalidTokenFormatException();
		}

		return new ResponseEntity<TokenDto>(tokenDto, status);
	}

	@ApiOperation(value = "[Header - refresh] 로그아웃", notes = "DB에서 리프레시 토큰 삭제 (클라이언트 측에서는 액세스 토큰과 리프레시 토큰 삭제)", response = MessageDto.class)
	@PostMapping("/logout")
	public ResponseEntity<MessageDto> logoutMember(@RequestBody @ApiParam(value = "기기 고유 ID") DeviceDto deviceDto, HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new InvalidTokenFormatException();
		}
		String refreshToken = authorization.replace("Bearer ", "");
		int flag = 0;
		HttpStatus status = HttpStatus.ACCEPTED;
		MessageDto messageDto = null;

//		로그아웃이나 회원가입할 때 리프레시 토큰을 이용하는지 액세스 토큰을 이용하는지 알아볼 것
//		다중 기기에서 로그인할 경우 토큰 테이블이 별도로 필요할 것으로 보임
		if ((flag = jwtUtility.checkToken(refreshToken, "refresh-token")) == 2) {
			String memberId = jwtUtility.getMemberId(refreshToken);
			int no = Integer.parseInt(memberId.replaceAll("[^0-9]", ""));
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("deviceId", deviceDto.getDeviceId());
			checkMap.put("no", no);
			
			if (refreshToken.equals(memberService.checkRefreshToken(checkMap))) {
				memberService.logoutMember(checkMap);
				messageDto = MessageDto.builder()
						.message("로그아웃 완료")
						.build();
				status = HttpStatus.OK;
			} else {
				log.info("존재하지 않는 리프레시 토큰 : {}", refreshToken);
				// 404 에러
				throw new MemberNotFoundException();
			}
		} else if (flag == 1) {
			log.info("토큰 만료 : {}", refreshToken);
			// 401 에러
			throw new TokenExpiredException();
		} else {
			log.info("토큰 형식 오류 : {}", refreshToken);
			// 401 에러
			throw new InvalidTokenFormatException();
		}

		return new ResponseEntity<MessageDto>(messageDto, status);
	}

//	중복된 코드 제거하도록 인터셉터 활용할 것
	@ApiOperation(value = "[Header - refresh] 회원탈퇴", notes = "DB에서 회원 정보 삭제", response = MessageDto.class)
	@DeleteMapping("/withdraw")
	public ResponseEntity<MessageDto> withdrawMember(@RequestBody @ApiParam(value = "기기 고유 ID") DeviceDto deviceDto, HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new InvalidTokenFormatException();
		}
		String refreshToken = authorization.replace("Bearer ", "");
		int flag = 0;
		HttpStatus status = HttpStatus.ACCEPTED;
		MessageDto messageDto = null;
		
//		checkToken 값이 2일 때만 (만료되지 않은 토큰) DB에서 삭제하도록 함
		if ((flag = jwtUtility.checkToken(refreshToken, "refresh-token")) == 2) {
			String memberId = jwtUtility.getMemberId(refreshToken);
			int no = Integer.parseInt(memberId.replaceAll("[^0-9]", ""));
			Map<String, Object> checkMap = new HashMap<>();
			checkMap.put("deviceId", deviceDto.getDeviceId());
			checkMap.put("no", no);
			
//			여기서 이중으로 DB 왔다갔다하지 않아도 되게 수정할 것
			if (refreshToken.equals(memberService.checkRefreshToken(checkMap))) {
				memberService.deleteToken(no);
				memberService.withdrawMember(no);
				messageDto = MessageDto.builder()
						.message("회원탈퇴 완료")
						.build();
				status = HttpStatus.OK;
			} else {
				log.info("존재하지 않는 리프레시 토큰 : {}", refreshToken);
				// 404 에러
				throw new MemberNotFoundException();
			}
		} else if (flag == 1) {
			log.info("토큰 만료 : {}", refreshToken);
			// 401 에러
			throw new TokenExpiredException();
		} else {
			log.info("토큰 형식 오류 : {}", refreshToken);
			// 401 에러
			throw new InvalidTokenFormatException();
		}

		return new ResponseEntity<MessageDto>(messageDto, status);
	}
	
	@ApiOperation(value = "[Header - access] 회원정보", notes = "DB에서 회원 정보 꺼내 반환", response = MemberDto.class)
	@GetMapping("/info")
	public ResponseEntity<MemberDto> getMemberInfo(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			throw new InvalidTokenFormatException();
		}
		String accessToken = authorization.replace("Bearer ", "");
		int flag = 0;
		HttpStatus status = HttpStatus.ACCEPTED;
		MemberDto memberDto = null;
		MemberDto memberFind = null;
		
		if ((flag = jwtUtility.checkToken(accessToken, "access-token")) == 2) {
			
			String memberId = jwtUtility.getMemberId(accessToken);
			int no = Integer.parseInt(memberId.replaceAll("[^0-9]", ""));
			String provider = memberId.replaceAll("[0-9]", "");
			memberDto = MemberDto.builder().no(no).provider(provider).build();
			System.out.println(memberDto);
			memberFind = memberService.findByMemberId(memberDto);
			System.out.println(memberFind);
			if (memberFind != null) {
				log.info("토큰 사용 가능 : {}", accessToken);
			} else {
				log.info("존재하지 않는 리프레시 토큰 : {}", accessToken);
				// 404 에러
				throw new MemberNotFoundException();
			}
		} else if (flag == 1) {
			log.info("토큰 만료 : {}", accessToken);
			// 401 에러
			throw new TokenExpiredException();
		} else {
			log.info("토큰 형식 오류 : {}", accessToken);
			// 401 에러
			throw new InvalidTokenFormatException();
		}

		return new ResponseEntity<MemberDto>(memberFind, status);
	}
	
	@ApiOperation(value = "중복 아이디 확인", notes = "DB에서 중복된 아이디가 존재하는지 확인", response = IdCheckDto.class)
	@GetMapping("/check")
	public ResponseEntity<IdCheckDto> checkId(@RequestParam @ApiParam(value = "체크할 아이디") String id) {
		boolean idCheck = false;
		String message = "사용 불가능";
		
		if (memberService.checkId(id) == null) {
			idCheck = true;
			message = "사용 가능";
		}
		
		IdCheckDto idCheckDto = IdCheckDto.builder()
				.idCheck(idCheck)
				.message(message)
				.build();

		return new ResponseEntity<IdCheckDto>(idCheckDto, HttpStatus.ACCEPTED);
	}
}
