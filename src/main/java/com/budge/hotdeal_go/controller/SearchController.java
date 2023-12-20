package com.budge.hotdeal_go.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @ApiOperation(value = "핫딜 정보 검색", notes = "게시글 제목 기준으로 데이터를 반환합니다.", response = List.class)
    @GetMapping("/info")
    public ResponseEntity<List<HotDealDto>> doSearchForTheHotDealInfo(
            @ApiParam(value = "얻어올 글의 제목", required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "0") @ApiParam(value = "0은 모두, 여러 값 입력시 1,2,3 (띄워쓰기 X) (default=0, 에펨코리아=1, 퀘이사존=2, 루리웹=3)") String siteno,
            @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호(defualt=1)") int pgno,
            @RequestParam(required = false, defaultValue = "50") @ApiParam(value = "페이지당 글의 수(default=50)") int npp)
            throws SQLException {
        List<HotDealDto> list = new ArrayList<>();

        if (title == null) {
            if (siteno.contains("0")) {
                return ResponseEntity.ok(searchService.getListAll(pgno, npp));
            }

            list = getList(siteno, pgno, npp);
        } else {
            if (siteno.contains("0")) {
                return ResponseEntity.ok(searchService.getList(title));
            }

            list = getListWithTitle(title, siteno, pgno, npp);
        }

        return ResponseEntity.ok(list);
    }

    @ApiOperation(value = "좋아요 Top3 게시글 반환", notes = "전체 커뮤니티에서 좋아요가 높은 3개의 게시글을 반환한다.", response = List.class)
    @GetMapping("/like/top3")
    public ResponseEntity<List<HotDealDto>> doGetTop3Like() throws SQLException {
        List<HotDealDto> list = searchService.getTop3Like();

        return ResponseEntity.ok(list);
    }

    // 선택된 사이트를 확인하여 핫딜 정보 가져오기
    public List<HotDealDto> getList(String siteno, int pgno, int npp) throws SQLException {
        List<HotDealDto> list = new ArrayList<>();
        String siteNames = "";
        int[] idx = new int[3];

        String[] arr = siteno.split(",");
        for (String no : arr) {
            switch (no) {
                case "1":
                    if (idx[0] == 1)
                        continue;
                    idx[0]++;
                    siteNames += "fmkorea,";
                    break;

                case "2":
                    if (idx[1] == 1)
                        continue;
                    idx[1]++;
                    siteNames += "quasarzone,";
                    break;

                case "3":
                    if (idx[2] == 1)
                        continue;
                    idx[2]++;
                    siteNames += "ruliweb,";
                    break;
            }
        }
        List<String> tmp = Arrays.asList(siteNames.split(","));

        list = searchService.getHotDealInfoBySiteName(pgno, npp, tmp);
        return list;
    }

    public List<HotDealDto> getListWithTitle(String title, String siteno, int pgno, int npp) throws SQLException {
        // List<HotDealDto> list = new ArrayList<>();

        // String[] arr = siteno.split(",");
        // for (String no : arr) {
        //     List<HotDealDto> tmp = new ArrayList<>();
        //     switch (no) {
        //         case "1":
        //             tmp = searchService.getListBySiteName(title, "fmkorea", pgno, npp);
        //             break;

        //         case "2":
        //             tmp = searchService.getListBySiteName(title, "quasarzone", pgno, npp);
        //             break;

        //         case "3":
        //             tmp = searchService.getListBySiteName(title, "ruliweb", pgno, npp);
        //             break;
        //     }
        //     list.addAll(tmp);
        // }

        List<HotDealDto> list = new ArrayList<>();
        String siteNames = "";
        int[] idx = new int[3];

        String[] arr = siteno.split(",");
        for (String no : arr) {
            switch (no) {
                case "1":
                    if (idx[0] == 1)
                        continue;
                    idx[0]++;
                    siteNames += "fmkorea,";
                    break;

                case "2":
                    if (idx[1] == 1)
                        continue;
                    idx[1]++;
                    siteNames += "quasarzone,";
                    break;

                case "3":
                    if (idx[2] == 1)
                        continue;
                    idx[2]++;
                    siteNames += "ruliweb,";
                    break;
            }
        }
        List<String> tmp = Arrays.asList(siteNames.split(","));
        list = searchService.getListBySiteName(title, tmp, pgno, npp);
        return list;
    }
}