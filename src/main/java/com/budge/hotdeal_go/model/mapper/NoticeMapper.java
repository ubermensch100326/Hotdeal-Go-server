package com.budge.hotdeal_go.model.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.budge.hotdeal_go.model.dto.ModifyNoticeDto;
import com.budge.hotdeal_go.model.dto.NoticeDetailDto;

@Mapper
public interface NoticeMapper {

	List<NoticeDetailDto> getNoticeList(Map<String, Object> param) throws SQLException;	
	
	int getTotalNoticeCount(Map<String, Object> param) throws SQLException;

	void writeNotice(NoticeDetailDto noticeDetailDto);

	NoticeDetailDto getNoticeDetail(int noticeNo);

	void updateHit(int noticeNo);

	void modifyNoticeDetail(ModifyNoticeDto modifyNoticeDto);

	void deleteNoticeDetail(int noticeNo);
}
