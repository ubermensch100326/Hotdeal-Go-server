package com.budge.hotdeal_go.model.mapper;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.budge.hotdeal_go.model.dto.HotDealDto;

@Mapper
public interface SearchMapper {
    boolean regist(List<HotDealDto> dto) throws SQLException;

    List<HotDealDto> getFmkorea(@Param("pgno") int pgno, @Param("npp") int npp) throws SQLException;

    List<HotDealDto> getQuasarZone(@Param("pgno") int pgno, @Param("npp") int npp) throws SQLException;

    List<HotDealDto> getRuliweb(@Param("pgno") int pgno, @Param("npp") int npp) throws SQLException;

    List<HotDealDto> getList(String title) throws SQLException;
}