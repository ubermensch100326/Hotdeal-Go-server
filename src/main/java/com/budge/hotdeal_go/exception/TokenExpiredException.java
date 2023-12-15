package com.budge.hotdeal_go.exception;

public class TokenExpiredException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TokenExpiredException() {
		super("토큰 만료");
	}
}
