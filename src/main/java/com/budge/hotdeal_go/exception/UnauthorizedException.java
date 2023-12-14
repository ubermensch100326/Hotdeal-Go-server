package com.budge.hotdeal_go.exception;

// 이건 나중에 쓸지말지 생각해볼 것
public class UnauthorizedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException() {
		super("Unauthorized");
	}
}