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
@ApiModel(value = "MessageDto : 응답 정보", description = "응답 메시지를 보여준다.")
public class MessageDto {
	@ApiModelProperty(value = "응답 메시지")
	private String message;
}
