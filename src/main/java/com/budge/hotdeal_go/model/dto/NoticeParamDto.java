package com.budge.hotdeal_go.model.dto;

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
@ApiModel(value = "NoticeParamDto : 공지사항 페이지 정보", description = "공지사항 페이지의 부가 정보를 나타낸다.")
public class NoticeParamDto {
	@ApiModelProperty(value = "키워드")
	private String keyword;
	@ApiModelProperty(value = "현재 페이지 번호")
	private int pgno;
	@ApiModelProperty(value = "페이지당 공지사항 글의 수")
	private int npp;
	
}
