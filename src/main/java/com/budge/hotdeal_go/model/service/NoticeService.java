package com.budge.hotdeal_go.model.service;

import com.budge.hotdeal_go.model.dto.ModifyNoticeDto;
import com.budge.hotdeal_go.model.dto.NoticeDetailDto;
import com.budge.hotdeal_go.model.dto.NoticeListDto;
import com.budge.hotdeal_go.model.dto.NoticeParamDto;

public interface NoticeService {

	NoticeListDto getNoticeList(NoticeParamDto noticeParamDto) throws Exception;

	void writeNotice(NoticeDetailDto noticeDetailDto);

	NoticeDetailDto getNoticeDetail(int noticeNo);

	void updateHit(int noticeNo);

	void modifyNoticeDetail(ModifyNoticeDto modifyNoticeDto);

	void deleteNoticeDetail(int noticeNo);
}
