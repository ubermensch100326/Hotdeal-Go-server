package com.budge.hotdeal_go.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.budge.hotdeal_go.exception.InvalidTokenFormatException;
import com.budge.hotdeal_go.exception.MemberNotFoundException;
import com.budge.hotdeal_go.exception.TokenExpiredException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MemberNotFoundException.class, TokenExpiredException.class, InvalidTokenFormatException.class})
    public ResponseEntity<String> handleCustomExceptions(Exception ex) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Internal Server Error";

        if (ex instanceof MemberNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
            message = ex.getMessage();
        } else if (ex instanceof TokenExpiredException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
        } else if (ex instanceof InvalidTokenFormatException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            message = ex.getMessage();
        }

        return ResponseEntity.status(httpStatus).body(message);
    }
}
