package com.budge.hotdeal_go.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/hotdeal")
@RequiredArgsConstructor
@Api(tags = "HotDealSearch", description = "Search Controller")
public class SearchController {

    private SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        super();
        this.searchService = searchService;
    }

    @ApiOperation(value = "핫딜 크롤링 정보 반환", notes = "해당 커뮤니티에서 핫딜 정보를 리스트로 반환한다.", response = List.class)
    @GetMapping("/info")
    public ResponseEntity<List<HotDealDto>> doSearchFmkorea(
            @RequestParam(required = false, defaultValue = ",") @ApiParam(value = "검색할 사이트 이름(default=0, 에펨코리아=1, 퀘이사존=2, 루리웹=3)") String siteno,
            @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호(defualt=1)") int pgno,
            @RequestParam(required = false, defaultValue = "50") @ApiParam(value = "페이지당 글의 수(default=50)") int npp)
            throws SQLException {
        List<HotDealDto> list = new ArrayList<>();

        if (siteno.contains("0")) {
            return ResponseEntity.ok(searchService.getListAll(pgno, npp));
        }

        String[] arr = siteno.split(",");
        for (String no : arr) {
            List<HotDealDto> tmp = new ArrayList<>();
            switch (no) {
                case "1":
                    tmp = searchService.getHotDealInfoBySiteName("fmkorea", pgno, npp);
                    break;

                case "2":
                    tmp = searchService.getHotDealInfoBySiteName("quasarzone", pgno, npp);
                    break;

                case "3":
                    tmp = searchService.getHotDealInfoBySiteName("ruliweb", pgno, npp);
                    break;
            }
            list.addAll(tmp);
        }
        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "핫딜 정보 검색", notes = "게시글 제목 기준으로 데이터를 반환합니다.", response = List.class)
    @GetMapping("/{title}")
    public ResponseEntity<List<HotDealDto>> doSearchForTheHotDealInfo(
            @ApiParam(value = "얻어올 글의 제목", required = true) String title,
            @RequestParam(required = false, defaultValue = ",") @ApiParam(value = "검색할 사이트 이름(default=0, 에펨코리아=1, 퀘이사존=2, 루리웹=3)") String siteno,
            @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호(defualt=1)") int pgno,
            @RequestParam(required = false, defaultValue = "50") @ApiParam(value = "페이지당 글의 수(default=50)") int npp)
            throws SQLException {

        List<HotDealDto> list = new ArrayList<>();

        if (siteno.contains("0")) {
            return ResponseEntity.ok(searchService.getList(title));
        }

        String[] arr = siteno.split(",");
        for (String no : arr) {
            List<HotDealDto> tmp = new ArrayList<>();
            switch (no) {
                case "1":
                    tmp = searchService.getListBySiteName(title, "fmkorea", pgno, npp);
                    break;

                case "2":
                    tmp = searchService.getListBySiteName(title, "quasarzone", pgno, npp);
                    break;

                case "3":
                    tmp = searchService.getListBySiteName(title, "ruliweb", pgno, npp);
                    break;
            }
            list.addAll(tmp);
        }

        return ResponseEntity.ok(list);
    }

    // @ApiOperation(value = "조회수 Top3 게시글 반환", notes = "전체 커뮤니티에서 조회수가 높은 3개의 게시글을
    // 반환한다.", response = List.class)
    // @GetMapping("/top3")
    // public ResponseEntity<List<HotDealDto>> doGetTop3View() {
    // List<HotDealDto> list = new ArrayList<>();
    // list = searchService.getTop3View();

    // return ResponseEntity.ok(list);
    // }

    @ApiOperation(value = "좋아요 Top3 게시글 반환", notes = "전체 커뮤니티에서 좋아요가 높은 3개의 게시글을 반환한다.", response = List.class)
    @GetMapping("/like/top3")
    public ResponseEntity<List<HotDealDto>> doGetTop3Like() throws SQLException {
        List<HotDealDto> list = searchService.getTop3Like();

        return ResponseEntity.ok(list);
    }
}