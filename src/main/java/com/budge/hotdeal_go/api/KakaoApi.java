package com.budge.hotdeal_go.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KakaoApi {

	public String getAccessToken(String code) {
		String accessToken = "";
		String refreshToken = "";
		String requestUrl = "https://kauth.kakao.com/oauth/token";

		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
			conn.setDoOutput(true);

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			StringBuilder sb = new StringBuilder();

			// 필수 쿼리 파라미터 세팅
			sb.append("grant_type=authorization_code");
			// 임시로 client api key 직접 넣어줌
			// 추후에 바꾸면서 kakao에 있는 프로젝트 삭제할 것
			sb.append("&client_id=").append("247fbba0160d9dbd31f7c1cf712806db");
			sb.append("&redirect_uri=").append("http://localhost/oauth/kakao");
			sb.append("&code=").append(code);

			bw.write(sb.toString());
			bw.flush();

			int responseCode = conn.getResponseCode();
			log.info("[KakaoApi.getAccessToken] responseCode = {}", responseCode);

			BufferedReader br;
			if (responseCode >= 200 && responseCode <= 300) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			String line = "";
			StringBuilder responseSb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				responseSb.append(line);
			}

			String result = responseSb.toString();
			log.info("responseBody = {}", result);

			JsonElement element = JsonParser.parseString(result);

			accessToken = element.getAsJsonObject().get("access_token").getAsString();
			refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	public Map<String, Object> getUserInfo(String accessToken) {
		Map<String, Object> userInfo = new HashMap<>();
	    String requestUrl = "https://kapi.kakao.com/v2/user/me";
		
	    try {
	    	URL url = new URL(requestUrl);
	    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    	conn.setRequestMethod("POST");
	        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
	        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
	        
	        int responseCode = conn.getResponseCode();
	        log.info("[KakaoApi.getUserInfo] responseCode : {}",  responseCode);

	        BufferedReader br;
	        if (responseCode >= 200 && responseCode <= 300) {
	            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        }
	        else {
	            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
	        }
	        
	    	String line = "";
	    	StringBuilder responseSb = new StringBuilder();
	    	while ((line = br.readLine()) != null) {
	    		responseSb.append(line);
	    	}
	    	
	    	String result = responseSb.toString();
	    	log.info("responseBody = {}", result);
	    	
			JsonElement element = JsonParser.parseString(result);

	        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
	        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
	        System.out.println("properties ##################################");
	        System.out.println(properties);
	        System.out.println("kakaoAccount ##################################");
	        System.out.println(kakaoAccount);
	        String nickname = properties.getAsJsonObject().get("nickname").getAsString();
//	        String email = kakaoAccount.getAsJsonObject().get("email").getAsString();
	        userInfo.put("nickname", nickname);
//	        userInfo.put("email", email);
	        System.out.println(userInfo);
			
//	        JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
//	        JsonObject kakaoAccount = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
//	        
//	        String nickname = properties.getAsJsonObject().get("profile_nickname").getAsString();
//	        String image = properties.getAsJsonObject().get("profile_image").getAsString();
//	        
//	    	userInfo.put("nickname", nickname);
//	    	userInfo.put("image", image);
	    	
	    	br.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    return userInfo;
		
	}
}
