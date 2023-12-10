package com.budge.hotdeal_go.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.model.dto.HotDealDto;
import com.budge.hotdeal_go.model.service.SearchService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/hotdeal")
@Api(tags = "HotDealSearch", description = "Search Controller")
public class SearchController {

    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        super();
        this.searchService = searchService;
    }

    @ApiOperation(value = "에펨코리아 핫딜 정보 반환", notes = "해당 커뮤니티에서 핫딜 정보를 리스트로 반환한다.", response = List.class)
    @GetMapping("/fmkorea")
    public ResponseEntity<List<HotDealDto>> doSearchFmkorea(
            @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호(defualt=1)") int pgno,
            @RequestParam(required = false, defaultValue = "50") @ApiParam(value = "페이지당 글의 수(default=50)") int npp)
            throws SQLException {
        List<HotDealDto> list = searchService.getFmkorea(pgno, npp);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "퀘이사존 핫딜 정보 반환", notes = "해당 커뮤니티에서 핫딜 정보를 리스트로 반환한다.", response = List.class)
    @GetMapping("/quasarzone")
    public ResponseEntity<List<HotDealDto>> doSearchQuasarZone(
            @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호(default=1)") int pgno,
            @RequestParam(required = false, defaultValue = "50") @ApiParam(value = "페이지당 글의 수(default=50)") int npp)
            throws SQLException {
        List<HotDealDto> list = searchService.getQuasarZone(pgno, npp);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "루리웹 핫딜 정보 반환", notes = "해당 커뮤니티에서 핫딜 정보를 리스트로 반환한다.", response = List.class)
    @GetMapping("/ruliweb")
    public ResponseEntity<List<HotDealDto>> doSearchRuliweb(
            @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호(default=1)") int pgno,
            @RequestParam(required = false, defaultValue = "50") @ApiParam(value = "페이지당 글의 수(default=50)") int npp)
            throws SQLException {
        List<HotDealDto> list = searchService.getRuliweb(pgno, npp);
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "핫딜 정보 검색", notes = "게시글 제목 기준으로 데이터를 반환합니다.", response = List.class)
    @GetMapping("/{title}")
    public ResponseEntity<List<HotDealDto>> doSearchForTheHotDealInfo(
            @PathVariable("title") @ApiParam(value = "얻어올 글의 제목", required = true) String title)
            throws SQLException {

        List<HotDealDto> list = searchService.getList(title);
        return ResponseEntity.ok(list);
    }
}