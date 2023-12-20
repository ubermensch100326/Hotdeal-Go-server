package com.budge.hotdeal_go.model.mapper;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.budge.hotdeal_go.model.dto.KeywordDto;

@Mapper
public interface FCMMapper {
    public String getFcmToken(String userId) throws SQLException;

    public void registFcmToken(@Param("userId") String userId, @Param("fcmToken") String fcmToken) throws SQLException;

    public void removeFcmToken(String userId) throws SQLException;

    public List<KeywordDto> getKeywordAll() throws SQLException;

    public List<KeywordDto> getKeywordByUserId(String userId) throws SQLException;

    public void registKeyword(@Param("userId")String userId, @Param("keywordName")String keywordName) throws SQLException;

    public void removeKeyword(@Param("userId")String userId, @Param("keywordName")String keywordName) throws SQLException;
}