package com.budge.hotdeal_go.model.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.budge.hotdeal_go.model.dto.KeywordDto;
import com.budge.hotdeal_go.model.mapper.FCMMapper;

@Service
public class FCMServiceImpl implements FCMService {

    private FCMMapper mapper;

    @Autowired
    public FCMServiceImpl(FCMMapper mapper) {
        super();
        this.mapper = mapper;
    }

    @Override
    public String getFcmToken(String userId) throws SQLException {
        return mapper.getFcmToken(userId);
    }

    @Override
    public void registFcmToken(String userId, String fcmToken) throws SQLException {
        mapper.registFcmToken(userId, fcmToken);
    }

    @Override
    public void removeFcmToken(String userId) throws SQLException {
        mapper.removeFcmToken(userId);
    }

    @Override
    public List<KeywordDto> getKeywordAll() throws SQLException {
        return mapper.getKeywordAll();
    }

    @Override
    public List<KeywordDto> getKeywordByUserId(String userId) throws SQLException {
        return mapper.getKeywordByUserId(userId);
    }

    @Override
    public void registKeyword(String userId, String keywordName) throws SQLException {
        mapper.registKeyword(userId, keywordName);
    }

    @Override
    public void removeKeyword(String userId, String keywordName) throws SQLException {
        mapper.removeKeyword(userId, keywordName);
    }
}