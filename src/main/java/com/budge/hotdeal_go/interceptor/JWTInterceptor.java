package com.budge.hotdeal_go.interceptor;

//https://kauth.kakao.com/oauth/authorize?client_id=ced03e3cc4043337974fa444e118cbb5&redirect_uri=http://localhost/member/oauth/kakao/test&response_type=code
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.budge.hotdeal_go.exception.InvalidTokenFormatException;
import com.budge.hotdeal_go.exception.MemberNotFoundException;
import com.budge.hotdeal_go.exception.TokenExpiredException;
import com.budge.hotdeal_go.exception.UnauthorizedException;
import com.budge.hotdeal_go.model.dto.MemberDto;
import com.budge.hotdeal_go.model.service.MemberService;
import com.budge.hotdeal_go.util.JWTUtility;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

	private final String HEADER_AUTH = "Authorization";

	private JWTUtility jwtUtility;
	private MemberService memberService;

	public JWTInterceptor(JWTUtility jwtUtility, MemberService memberService) {
		super();
		this.jwtUtility = jwtUtility;
		this.memberService = memberService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
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
		
        // 헤더에 있는 액세스 토큰 정보 확인
		String authorization = request.getHeader(HEADER_AUTH);
		if (!authorization.startsWith("Bearer ")) {
			throw new InvalidTokenFormatException();
		}
		String accessToken = authorization.replace("Bearer ", "");
		int flag = 0;
		if (accessToken != null && (flag = jwtUtility.checkToken(accessToken, "access-token")) != 0) {
			if (flag == 2) {
				String memberId = jwtUtility.getMemberId(accessToken);
				int no = Integer.parseInt(memberId.replaceAll("[^0-9]", ""));
				String provider = memberId.replaceAll("[0-9]", "");
				MemberDto memberDto = MemberDto.builder().no(no).provider(provider).build();
				MemberDto memberFind = memberService.findByMemberId(memberDto);
				
				if (memberFind != null) {
					log.info("토큰 사용 가능 : {}", accessToken);
					return true;
				} else {
					log.info("회원 목록에 사용자 부재 : {}", accessToken);
					// 404 에러
					throw new MemberNotFoundException();
				}
			} else {
				log.info("토큰 만료 : {}", accessToken);
				// 401 에러
				throw new TokenExpiredException();
			}

		} else {
			log.info("토큰 형식 오류 : {}", accessToken);
			// 401 에러
			throw new InvalidTokenFormatException();
		}
	}
}
