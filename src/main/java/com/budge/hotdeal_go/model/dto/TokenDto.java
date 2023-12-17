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
@ApiModel(value = "TokenDto : 토큰 정보", description = "액세스 토큰과 리프레시 토큰을 나타낸다.")
public class TokenDto {
	@ApiModelProperty(value = "액세스 토큰")
	private String accessToken;
	@ApiModelProperty(value = "리프레시 토큰")
	private String refreshToken;
	@ApiModelProperty(value = "응답 메시지")
	private String message;
}
