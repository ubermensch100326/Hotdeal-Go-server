package com.budge.hotdeal_go.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.api.KakaoApi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/oauth")
@Api("소셜 로그인 컨트롤러 API V1")
@Slf4j
public class OAuthController {
	
	private KakaoApi kakaoApi;
	
	@Autowired
	public OAuthController(KakaoApi kakaoApi) {
		super();
		this.kakaoApi = kakaoApi;
	}

	@GetMapping("/kakao")
	public ResponseEntity<?> kakao(@RequestParam @ApiParam(value = "카카오 로그인", required = true) Map<String, String> map) {
		System.out.println("인가 코드 : ");
		String code = map.get("code");
		System.out.println(code);
		System.out.println("=======================================");
		System.out.println("인가 코드로 받아온 액세스 토큰 정보 : ");
		String accessToken = kakaoApi.getAccessToken(code);
		System.out.println(accessToken);
		System.out.println("=======================================");
		System.out.println("액세스 토큰으로 받아온 사용자 정보 : ");
		System.out.println("=======================================");
		Map<String, Object> userInfo = kakaoApi.getUserInfo(accessToken);
        String nickname = (String) userInfo.get("nickname");
        System.out.println("nickname = " + nickname);
//        String email = (String) userInfo.get("email");
//        System.out.println("email = " + email);
		
		return null;
	}
	
}
