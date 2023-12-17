package com.budge.hotdeal_go.model.service;

import com.budge.hotdeal_go.model.dto.NoticeListDto;
import com.budge.hotdeal_go.model.dto.NoticeParamDto;

public interface NoticeService {

	NoticeListDto getNoticeList(NoticeParamDto noticeParamDto) throws Exception;
}
