package com.budge.hotdeal_go.model.mapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.budge.hotdeal_go.model.dto.NoticeDto;

@Mapper
public interface NoticeMapper {

	List<NoticeDto> getNoticeList(Map<String, Object> param) throws SQLException;	
	
	int getTotalNoticeCount(Map<String, Object> param) throws SQLException;
}
