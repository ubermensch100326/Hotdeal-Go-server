package com.budge.hotdeal_go.model.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
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
    public List<HotDealDto> getHotDealInfoBySiteName(int pgno, int npp, List<String> siteNames) throws SQLException {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("pgno", pgno);
        parameter.put("npp", npp);
        parameter.put("siteNames", siteNames);
        return searchMapper.getHotDealInfoBySiteName(parameter);
    }

    @Override
    public List<HotDealDto> getList(String title) throws SQLException {
        return searchMapper.getList(title);
    }

    @Override
    public List<HotDealDto> getListAll(int pgno, int npp) throws SQLException {
        return searchMapper.getListAll(pgno, npp);
    }

    @Override
    public List<HotDealDto> getListBySiteName(String title, List<String> siteNames, int pgno, int npp)
            throws SQLException {
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("title", title);
        parameter.put("siteNames", siteNames);
        parameter.put("pgno", pgno);
        parameter.put("npp", npp);
        return searchMapper.getListBySiteName(parameter);
    }

    @Override
    public List<HotDealDto> getTop3View() throws SQLException {
        return searchMapper.getTop3View();
    }

    @Override
    public List<HotDealDto> getTop3Like() throws SQLException {
        return searchMapper.getTop3Like();
    }
}