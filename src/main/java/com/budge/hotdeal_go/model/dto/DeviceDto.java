package com.budge.hotdeal_go.model.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@ApiModel(value = "DeviceDto : 기기 정보", description = "기기 고유 ID를 나타낸다.")
public class DeviceDto {
	@ApiModelProperty(value = "기기 고유 ID")
	private String deviceId;
}
