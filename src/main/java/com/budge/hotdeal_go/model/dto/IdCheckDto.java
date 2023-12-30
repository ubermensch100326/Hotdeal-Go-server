package com.budge.hotdeal_go.model.dto;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "IdCheckDto : 중복 아이디 여부", description = "사용 가능한 아이디인지 확인")
public class IdCheckDto {
	private boolean idCheck;
	private String message;
}
