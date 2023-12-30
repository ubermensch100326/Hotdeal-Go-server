package com.budge.hotdeal_go.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// 편의상 정규화 생각하지 않고 우선 구현부터 함
// 추후 DB 모델링 다듬어지면 수정 예정
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "MemberDto : 회원 정보", description = "회원의 상세 정보를 나타낸다.")
public class MemberDto {
	@ApiModelProperty(value = "회원 번호")
	private int no;
	@ApiModelProperty(value = "회원 닉네임")
	private String nickname;
	@ApiModelProperty(value = "아이디 (사이트 자체적인 회원가입)")
	private String id;
	@ApiModelProperty(value = "비밀번호 (사이트 자체적인 회원가입)")
	private String password;
	@ApiModelProperty(value = "소셜 로그인 제공자")
	private String provider;
	@ApiModelProperty(value = "소셜 로그인 식별자")
	private String providerId;
	@ApiModelProperty(value = "연락 이메일")
	private String contactEmail;
	@ApiModelProperty(value = "회원가입 시간")
	private String registerTime;
	@ApiModelProperty(value = "관리자 여부 (0/1)")
	private int admin;
}