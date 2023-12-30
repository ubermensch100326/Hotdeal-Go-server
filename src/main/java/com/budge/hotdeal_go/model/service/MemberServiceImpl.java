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
	public String checkRefreshToken(Map<String, Object> checkMap) {
		return memberMapper.checkRefreshToken(checkMap);
	}

	@Override
	public void logoutMember(Map<String, Object> checkMap) {
		memberMapper.logoutMember(checkMap);
	}

	@Override
	public void withdrawMember(int no) {
		memberMapper.withdrawMember(no);
	}

	@Override
	public void deleteToken(int no) {
		memberMapper.deleteToken(no);
	}

	@Override
	public MemberDto checkId(String id) {
		return memberMapper.checkId(id);
	}

}
