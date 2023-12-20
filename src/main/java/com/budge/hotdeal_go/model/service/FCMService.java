package com.budge.hotdeal_go.model.service;

import java.sql.SQLException;
import java.util.List;

import com.budge.hotdeal_go.model.dto.KeywordDto;

public interface FCMService {
    public String getFcmToken(String userId) throws SQLException;

    public void registFcmToken(String userId, String fcmToken) throws SQLException;

    public void removeFcmToken(String userId) throws SQLException;

    public List<KeywordDto> getKeywordAll() throws SQLException;

    public List<KeywordDto> getKeywordByUserId(String userId) throws SQLException;

    public void registKeyword(String userId, String keywordName) throws SQLException;

    public void removeKeyword(String userId, String keywordName) throws SQLException;
}