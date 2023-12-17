package com.budge.hotdeal_go.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.budge.hotdeal_go.model.dto.ModifyNoticeDto;
import com.budge.hotdeal_go.model.dto.NoticeDetailDto;
import com.budge.hotdeal_go.model.dto.NoticeListDto;
import com.budge.hotdeal_go.model.dto.NoticeParamDto;
import com.budge.hotdeal_go.model.mapper.NoticeMapper;

@Service
public class NoticeServiceImpl implements NoticeService {
	
	private NoticeMapper noticeMapper;

	@Autowired
	public NoticeServiceImpl(NoticeMapper noticeMapper) {
		super();
		this.noticeMapper = noticeMapper;
	}
	
	@Override
	public NoticeListDto getNoticeList(NoticeParamDto noticeParamDto) throws Exception {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("keyword", noticeParamDto.getKeyword());
		int currentPage = noticeParamDto.getPgno();
		int noticePerPage = noticeParamDto.getNpp();
		int start = currentPage * noticePerPage - noticePerPage;
		param.put("start", start);
		param.put("size", noticePerPage);
		List<NoticeDetailDto> list = noticeMapper.getNoticeList(param);
		
		int totalNoticeCount = noticeMapper.getTotalNoticeCount(param);
		int totalPageCount = (totalNoticeCount - 1) / noticePerPage + 1;
		
		NoticeListDto noticeListDto = new NoticeListDto();
		noticeListDto.setNoticeList(list);
		noticeListDto.setCurrentPage(currentPage);
		noticeListDto.setTotalPageCount(totalPageCount);
		
		return noticeListDto;
	}

	@Override
	public void writeNotice(NoticeDetailDto noticeDetailDto) {
		noticeMapper.writeNotice(noticeDetailDto);
	}

	@Override
	public NoticeDetailDto getNoticeDetail(int noticeNo) {
		return noticeMapper.getNoticeDetail(noticeNo);
	}

	@Override
	public void updateHit(int noticeNo) {
		noticeMapper.updateHit(noticeNo);
	}

	@Override
	public void modifyNoticeDetail(ModifyNoticeDto modifyNoticeDto) {
		noticeMapper.modifyNoticeDetail(modifyNoticeDto);
	}

	@Override
	public void deleteNoticeDetail(int noticeNo) {
		noticeMapper.deleteNoticeDetail(noticeNo);
	}
	
}
