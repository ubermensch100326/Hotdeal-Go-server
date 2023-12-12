package com.budge.hotdeal_go.model.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.budge.hotdeal_go.model.dto.HotDealDto;

public interface SearchService {
    // 크롤링 정보를 DB에 등록하는 함수
    boolean regist(List<HotDealDto> dto) throws SQLException;

    // 전체 커뮤니티에서 핫딜 정보를 가져오는 함수
    List<HotDealDto> getListAll(@Param("pgno") int pgno, @Param("npp") int npp) throws SQLException;

    // 해당 커뮤니티에서 핫딜 정보를 가져오는 함수
    List<HotDealDto> getHotDealInfoBySiteName(String siteName, @Param("pgno") int pgno, @Param("npp") int npp)
            throws SQLException;

    // 전체 커뮤니티에서 제목에 해당하는 게시글을 가져오는 함수
    List<HotDealDto> getList(String title) throws SQLException;

    // 해당 커뮤니티에서 제목에 해당하는 게시글을 가져오는 함수
    List<HotDealDto> getListBySiteName(String title, String siteName, @Param("pgno") int pgno, @Param("npp") int npp)
            throws SQLException;

    // 조회수 탑3 게시글 가져오기
    List<HotDealDto> getTop3View() throws SQLException;

    // 좋아요 탑3 게시글 가져오기
    List<HotDealDto> getTop3Like() throws SQLException;
}
