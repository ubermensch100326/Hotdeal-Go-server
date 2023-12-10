package com.budge.hotdeal_go.model.service;

import java.sql.SQLException;
import java.util.List;

import com.budge.hotdeal_go.model.dto.HotDealDto;

public interface SearchService {
    boolean regist(List<HotDealDto> dto) throws SQLException;

    List<HotDealDto> getFmkorea(int pgno, int npp) throws SQLException;

    List<HotDealDto> getQuasarZone(int pgno, int npp) throws SQLException;

    List<HotDealDto> getRuliweb(int pgno, int npp) throws SQLException;

    List<HotDealDto> getList(String title) throws SQLException;
}
