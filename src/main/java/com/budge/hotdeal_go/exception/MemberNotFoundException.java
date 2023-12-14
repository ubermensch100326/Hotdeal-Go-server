package com.budge.hotdeal_go.exception;

public class MemberNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MemberNotFoundException() {
		super("Member not found");
	}
}
