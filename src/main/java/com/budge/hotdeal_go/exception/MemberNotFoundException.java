package com.budge.hotdeal_go.exception;

public class MemberNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MemberNotFoundException() {
		super("해당 멤버가 멤버 목록에 부재");
	}
}
