package com.budge.hotdeal_go.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "MemberDto : 회원 정보", description = "회원의 상세 정보를 나타낸다.")
public class RegisterDto {
	@ApiModelProperty(value = "회원 닉네임")
	private String nickname;
	@ApiModelProperty(value = "아이디 (사이트 자체적인 회원가입)")
	private String id;
	@ApiModelProperty(value = "비밀번호 (사이트 자체적인 회원가입)")
	private String password;
	@ApiModelProperty(value = "연락 이메일")
	private String contactEmail;
	@ApiModelProperty(value = "성별 (예: female)")
	private String gender;
	@ApiModelProperty(value = "나이대 (예: 20~29)")
	private String ageRange;
	@ApiModelProperty(value = "생일 (예: 0125)")
	private String birthday;
}
