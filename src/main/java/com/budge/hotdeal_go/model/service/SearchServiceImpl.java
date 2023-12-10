package com.budge.hotdeal_go.model.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.budge.hotdeal_go.model.dto.HotDealDto;
import com.budge.hotdeal_go.model.mapper.SearchMapper;

@Service
public class SearchServiceImpl implements SearchService {

    private SearchMapper searchMapper;

    public SearchServiceImpl(SearchMapper searchMapper) {
        super();
        this.searchMapper = searchMapper;
    }

    @Override
    public boolean regist(List<HotDealDto> dto) throws SQLException {
        return searchMapper.regist(dto);
    }

    @Override
    public List<HotDealDto> getFmkorea(int pgno, int npp) throws SQLException {
        return searchMapper.getFmkorea(pgno, npp);
    }

    @Override
    public List<HotDealDto> getQuasarZone(int pgno, int npp) throws SQLException {
        return searchMapper.getQuasarZone(pgno, npp);
    }

    @Override
    public List<HotDealDto> getRuliweb(int pgno, int npp) throws SQLException {
        return searchMapper.getRuliweb(pgno, npp);
    }

    @Override
    public List<HotDealDto> getList(String title) throws SQLException {
        return searchMapper.getList(title);
    }
}