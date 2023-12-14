package com.budge.hotdeal_go.exception;

public class InvalidTokenFormatException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidTokenFormatException() {
		super("Unauthorized : Invalid token format");
	}
}
