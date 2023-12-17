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
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
@ApiModel(value = "LoginDto : 로그인 아이디, 비밀번호", description = "로그인에 쓰이는 정보를 나타낸다.")
public class LoginDto {
	@ApiModelProperty(value = "아이디 (사이트 자체적인 회원가입)")
	private String id;
	@ApiModelProperty(value = "비밀번호 (사이트 자체적인 회원가입)")
	private String password;
}
