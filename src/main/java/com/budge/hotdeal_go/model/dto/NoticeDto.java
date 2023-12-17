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
@ApiModel(value = "NoticeDto : 공지사항 정보", description = "공지사항의 상세 정보를 나타낸다.")
public class NoticeDto {
	@ApiModelProperty(value = "글번호")
	private int no;
	@ApiModelProperty(value = "글제목")
	private String title;
	@ApiModelProperty(value = "글내용")
	private String content;
	@ApiModelProperty(value = "조회수")
	private int hit;
	@ApiModelProperty(value = "작성일")	
	private String registerTime;
	@ApiModelProperty(value = "멤버 번호")
	private int memberNo;
}
