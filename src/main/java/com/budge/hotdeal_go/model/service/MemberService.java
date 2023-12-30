package com.budge.hotdeal_go.model.service;

import java.util.Map;

import com.budge.hotdeal_go.model.dto.LoginDto;
import com.budge.hotdeal_go.model.dto.MemberDto;

public interface MemberService {

	MemberDto findByProviderId(MemberDto memberDto);

	void registerMember(MemberDto memberDto);

	void saveRefreshToken(Map<String, Object> saveMap);

	MemberDto loginMember(LoginDto loginDto);

	MemberDto findByMemberId(MemberDto memberDto);

	String checkRefreshToken(Map<String, Object> checkMap);

	void logoutMember(Map<String, Object> checkMap);

	void withdrawMember(int no);

	void deleteToken(int no);

	MemberDto checkId(String id);
}
