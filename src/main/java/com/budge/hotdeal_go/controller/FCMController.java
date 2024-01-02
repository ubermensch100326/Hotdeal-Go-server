package com.budge.hotdeal_go.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.model.dto.KeywordDto;
import com.budge.hotdeal_go.model.service.FCMService;
import com.budge.hotdeal_go.model.service.FirebaseService;
import com.google.auth.oauth2.GoogleCredentials;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/fcm")
@AllArgsConstructor
public class FCMController {

    private final FirebaseService firebaseService;

    private FCMService fcmService;

    @ApiOperation(value = "(테스트용) 알림 날리기")
    @PostMapping("/sendNotification")
    public void doPostNoti(
            @RequestParam(required = true) @ApiParam(value = "보낼 사용자 ID") String userId,
            @RequestParam(required = true) @ApiParam(value = "보낼 내용") String content) throws SQLException {

        String fcmToken = fcmService.getFcmToken(userId);
        String response = firebaseService.sendNotification(content, fcmToken);
        System.out.println(response);
    }

    @ApiOperation(value = "FCM 토큰 등록", notes = "사용자 ID와 FCM 토큰을 저장한다.")
    @PostMapping("/token")
    public ResponseEntity<String> doRegistFcmToken(
            @RequestParam(required = true) @ApiParam(value = "등록할 사용자 ID") String userId,
            @RequestParam(required = true) @ApiParam(value = "등록할 키워드 이름") String fcmToken) {
        try {
            fcmService.removeFcmToken(userId);
            fcmService.registFcmToken(userId, fcmToken);
            return ResponseEntity.ok("FCM_Token registed successfully");
        } catch (Exception e) {
            // 409 반환
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Fail to regist FCM_Token!!");
        }
    }

    @ApiOperation(value = "FCM 토큰 삭제", notes = "사용자 ID의 FCM 토큰을 삭제한다.")
    @DeleteMapping("/token")
    public ResponseEntity<String> doRemoveFcmToken(
            @RequestParam(required = true) @ApiParam(value = "삭제할 사용자 ID") String userId) throws SQLException {
        try {
            fcmService.removeFcmToken(userId);
            return ResponseEntity.ok("FCM_Token removed successfully");
        } catch (Exception e) {
            // 404 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found FCM_Token!!");
        }
    }

    @ApiOperation(value = "키워드 목록 조회", notes = "사용자 ID 기준으로 등록된 키워드 목록을 가져온다.")
    @GetMapping("/Keyword")
    public ResponseEntity<List<KeywordDto>> doGetKeyword(
            @RequestParam(required = true) @ApiParam(value = "가져올 사용자 ID") String userId) throws SQLException {
        List<KeywordDto> list = fcmService.getKeywordByUserId(userId);

        if (list.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @ApiOperation(value = "키워드 알림 등록", notes = "사용자 ID와 키워드 이름을 등록한다.")
    @PostMapping("/keyword")
    public ResponseEntity<String> doRegistKeyword(
            @RequestParam(required = true) @ApiParam(value = "등록할 사용자 ID") String userId,
            @RequestParam(required = true) @ApiParam(value = "등록할 키워드 이름") String keywordName) {
        try {
            fcmService.registKeyword(userId, keywordName);
            return ResponseEntity.ok("Keyword registered successfully");
        } catch (Exception e) {
            // 409 반환
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate entry!!");
        }
    }

    @ApiOperation(value = "키워드 알림 삭제", notes = "사용자 ID와 키워드 이름이 일치한 값을 삭제한다.")
    @DeleteMapping("/keyword")
    public ResponseEntity<String> doRemoveKeyword(
            @RequestParam(required = true) @ApiParam(value = "삭제할 사용자 ID") String userId,
            @RequestParam(required = true) @ApiParam(value = "삭제할 키워드 이름") String keywordName) {

        try {
            fcmService.removeKeyword(userId, keywordName);
            return ResponseEntity.ok("Keyword removed successfully");
        } catch (Exception e) {
            // 404 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found entry!!");
        }
    }
}