package com.budge.hotdeal_go.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Controller
@RequestMapping("/")
@Api("홈 컨트롤러 API V1")
@Slf4j
public class HomeController {

	@ApiOperation(value = "홈화면", notes = "HttpURLConnection 테스트용", response = String.class)
	@GetMapping("/1")
	public String home1(Model model) {

		HttpURLConnection conn = null;
		BufferedReader br = null;

		try {
			URL url = new URL("http://13.125.124.61/hotdeal/info");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response = new StringBuilder();
			String readLine = null;

			while ((readLine = br.readLine()) != null) {
				response.append(readLine);
			}

			String result = response.toString();

			model.addAttribute("text", result);

//			두번째 요청
			url = new URL("http://13.125.124.61/hotdeal/like/top3");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder response2 = new StringBuilder();
			readLine = null;

			while ((readLine = br.readLine()) != null) {
				response2.append(readLine);
			}

			String result2 = response2.toString();

			model.addAttribute("text2", result2);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.disconnect();
		}

		return "index";
	}

	@ApiOperation(value = "홈화면", notes = "RestTemplate 테스트용", response = String.class)
	@GetMapping("/2")
	public String home2(Model model) {

		RestTemplate restTemplate = new RestTemplate();
	    StopWatch stopWatch = new StopWatch();

		try {
		    stopWatch.start();
			String result = restTemplate.getForObject("http://13.125.124.61/hotdeal/info", String.class);
			model.addAttribute("text", result);
		    stopWatch.stop();
		    System.out.println(result);
		    System.out.println(stopWatch.prettyPrint());
		    
		    stopWatch.start();
			String result2 = restTemplate.getForObject("http://13.125.124.61/hotdeal/like/top3", String.class);
			model.addAttribute("text2", result2);
		    stopWatch.stop();
		    System.out.println(result2);
		    System.out.println(stopWatch.prettyPrint());
		    
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "index";
	}

	@GetMapping("/3")
	@ResponseBody
	public Mono<String> home3() {
	    WebClient webClient = WebClient.create();
	    
	    StopWatch stopWatch = new StopWatch();
	    
	    stopWatch.start();
	    Mono<String> result1Mono = webClient.get().uri("http://13.125.124.61/hotdeal/info")
	            .retrieve()
	            .bodyToMono(String.class)
	            .doOnSuccess(result -> System.out.println("Result 1: " + result));
	    stopWatch.stop();
	    System.out.println(stopWatch.prettyPrint());

	    stopWatch.start();
	    Mono<String> result2Mono = webClient.get().uri("http://13.125.124.61/hotdeal/like/top3")
	            .retrieve()
	            .bodyToMono(String.class)
	            .doOnSuccess(result -> System.out.println("Result 2: " + result));
	    stopWatch.stop();
	    
	    System.out.println(stopWatch.prettyPrint());
	    
	    return result1Mono.then(result2Mono)
	            .thenReturn("처리 완료");
	}
}
