package com.budge.hotdeal_go.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.model.dto.NotiDto;
import com.budge.hotdeal_go.model.service.FirebaseService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/")
@AllArgsConstructor
@Slf4j
public class FCMController {

    private final FirebaseService firebaseService;

    @PostMapping
    public void createNotification(@RequestBody NotiDto notiDto) {
        String response = firebaseService.sendNotification(notiDto.getToken(), notiDto.getContent());
        log.info(response);
    }

}
