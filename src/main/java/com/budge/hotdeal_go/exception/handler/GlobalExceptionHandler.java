package com.budge.hotdeal_go.exception.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.budge.hotdeal_go.exception.InvalidTokenFormatException;
import com.budge.hotdeal_go.exception.MemberNotFoundException;
import com.budge.hotdeal_go.exception.NoticeException;
import com.budge.hotdeal_go.exception.TokenExpiredException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MemberNotFoundException.class, TokenExpiredException.class, InvalidTokenFormatException.class, NoticeException.class})
    public ResponseEntity<Map<String, Object>> handleCustomExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		Map<String, Object> responseMap = new HashMap<String, Object>();

        if (ex instanceof MemberNotFoundException) {
        	status = HttpStatus.NOT_FOUND;
            responseMap.put("message", ex.getMessage());
        } else if (ex instanceof TokenExpiredException) {
        	status = HttpStatus.UNAUTHORIZED;
            responseMap.put("message", ex.getMessage());
        } else if (ex instanceof InvalidTokenFormatException) {
        	status = HttpStatus.UNAUTHORIZED;
            responseMap.put("message", ex.getMessage());
        } else {
        	responseMap.put("message", ex.getMessage());
        }

		return new ResponseEntity<Map<String, Object>>(responseMap, status);
    }
}
