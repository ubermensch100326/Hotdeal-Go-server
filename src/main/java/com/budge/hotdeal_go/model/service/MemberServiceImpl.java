package com.budge.hotdeal_go.model.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.budge.hotdeal_go.model.dto.LoginDto;
import com.budge.hotdeal_go.model.dto.MemberDto;
import com.budge.hotdeal_go.model.mapper.MemberMapper;

@Service
public class MemberServiceImpl implements MemberService {
	
	private MemberMapper memberMapper;
	
	@Autowired
	public MemberServiceImpl(MemberMapper memberMapper) {
		super();
		this.memberMapper = memberMapper;
	}

	@Override
	public MemberDto findByProviderId(MemberDto memberDto) {
		return memberMapper.findByProviderId(memberDto);
	}
	
	@Override
	public MemberDto findByMemberId(MemberDto memberDto) {
		return memberMapper.findByMemberId(memberDto);
	}

	@Override
	public void registerMember(MemberDto memberDto) {
		memberMapper.registerMember(memberDto);
	}

	@Override
	public void saveRefreshToken(Map<String, Object> saveMap) {
		memberMapper.saveRefreshToken(saveMap);
	}

	@Override
	public MemberDto loginMember(LoginDto loginDto) {
		return memberMapper.loginMember(loginDto);
	}

	@Override
	public MemberDto checkRefreshToken(String refreshToken) {
		return memberMapper.checkRefreshToken(refreshToken);
	}

	@Override
	public void logoutMember(String refreshToken) {
		memberMapper.logoutMember(refreshToken);
	}

	@Override
	public void withdrawMember(String refreshToken) {
		memberMapper.withdrawMember(refreshToken);
	}

}
