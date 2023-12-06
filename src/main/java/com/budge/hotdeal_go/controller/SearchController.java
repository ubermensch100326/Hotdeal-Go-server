package com.budge.hotdeal_go.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.model.dto.HotDealDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/hotdeal")
@Api(tags = "핫딜 관련 정보", description = "Version 1.0")
public class SearchController {

    @ApiOperation(value = "에펨코리아 사이트 핫딜", notes = "해당 커뮤니티에서 핫딜 정보를 리스트로 반환한다.", response = List.class)
    @GetMapping("/fmkorea")
    public ResponseEntity<List<HotDealDto>> doSearchFmkorea() throws SQLException {
        List<HotDealDto> fmKorea = getFmKorea();
        List<HotDealDto> quasarZone = getQuasarZone();

        if (fmKorea.size() != 0) {
            return ResponseEntity.ok(fmKorea);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @ApiOperation(value = "퀘이사존 사이트 핫딜", notes = "해당 커뮤니티에서 핫딜 정보를 리스트로 반환한다.", response = List.class)
    @GetMapping("/quasarzone")
    public ResponseEntity<List<HotDealDto>> doSearchQuasarZone() throws SQLException {
        List<HotDealDto> quasarZone = getQuasarZone();

        if (quasarZone.size() != 0) {
            return ResponseEntity.ok(quasarZone);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @ApiOperation(value = "루리웹 사이트 핫딜", notes = "해당 커뮤니티에서 핫딜 정보를 리스트로 반환한다.", response = List.class)
    @GetMapping("/ruliweb")
    public ResponseEntity<List<HotDealDto>> doSearchPpomppu() throws SQLException {
        List<HotDealDto> ruliweb = getRuliweb();

        if (ruliweb.size() != 0) {
            return ResponseEntity.ok(ruliweb);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    public static List<HotDealDto> getFmKorea() {
        List<HotDealDto> list = new ArrayList<>();

        String url = "https://www.fmkorea.com/hotdeal";

        try {
            // 해당 URL에서 HTML 가져오기
            Document document = Jsoup.connect(url).get();

            // 가져온 HTML에서 특정 요소(예: 글 제목) 추출
            Elements titles = document.select("h3.title > a");
            Elements prices = document.select("span:contains(가격)");
            Elements lis = document.select("li.li");
            Elements shippingFees = document.select("span:contains(배송)");
            Elements purchasingPlaces = document.select("span:contains(쇼핑몰)");
            Elements times = document.select("span.regdate");
            // 추출된 요소 출력
            int i = 0;
            for (Element title : titles) {
                HotDealDto dto = new HotDealDto();
                dto.setTitle(title.text());
                dto.setPrice(prices.get(i).selectFirst("a").text().replace("원", "").trim());
                dto.setUrl("https://www.fmkorea.com" + title.attr("href"));
                dto.setShippingFee(shippingFees.get(i).selectFirst("a").text());
                dto.setPurchasingPlace(purchasingPlaces.get(i).selectFirst("a").text());
                dto.setTime(times.get(i).text());

                if (lis.get(i).select("a.pc_voted_count").text().equals("")) {
                    dto.setLikeCnt(Long.parseLong("0"));
                } else {
                    dto.setLikeCnt(Long.parseLong(lis.get(i).select("a.pc_voted_count > span.count").text()));
                }

                list.add(dto);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<HotDealDto> getQuasarZone() {
        List<HotDealDto> list = new ArrayList<>();

        String url = "https://quasarzone.com/bbs/qb_saleinfo";

        try {
            // 해당 URL에서 HTML 가져오기
            Document document = Jsoup.connect(url).get();

            // 가져온 HTML에서 특정 요소(예: 글 제목) 추출
            Elements trs = document.select("tr");
            Elements times = document.select("span.date");

            for (int i = 1; i < trs.size(); i++) {
                HotDealDto dto = new HotDealDto();

                Element tr = trs.get(i);

                String str = tr.select("span.ellipsis-with-reply-cnt").text();
                if (str.equals(""))
                    continue;

                String regex = "\\[([^\\]]+)\\]";
                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String marketName = matcher.group(1);
                    dto.setPurchasingPlace(marketName.replace("[", "").replace("]", "").trim());

                    String title = str.replaceFirst("\\[([^\\]]+)\\]", "").trim();
                    dto.setTitle(title);
                }

                dto.setTitle(tr.select("span.ellipsis-with-reply-cnt").text());

                String price = tr.select("span.text-orange").text();
                dto.setPrice(price.replace("￦", "").replace("(KRW)", "").trim());

                dto.setShippingFee(tr.select("span:contains(배송비)").text().replace("배송비", "").trim());
                dto.setLikeCnt(Long.parseLong(tr.select("span.num").text()));
                dto.setUrl("https://quasarzone.com" + tr.select("a.subject-link").attr("href"));
                dto.setTime(times.get(i - 1).text());
                list.add(dto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<HotDealDto> getRuliweb() {
        List<HotDealDto> list = new ArrayList<>();

        String url = "https://bbs.ruliweb.com/market/board/1020";

        try {
            // 해당 URL에서 HTML 가져오기
            Document document = Jsoup.connect(url).get();

            // 가져온 HTML에서 특정 요소(예: 글 제목) 추출
            Elements trs = document.select("tr.table_body");
            Elements times = document.select("td.time");

            for (int i = 8; i < trs.size(); i++) {
                HotDealDto dto = new HotDealDto();

                Element tr = trs.get(i);

                String str = tr.select("a.deco").text();

                String regex = "\\[([^\\]]+)\\]";
                Pattern pattern = Pattern.compile(regex);

                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    String marketName = matcher.group(1);
                    dto.setPurchasingPlace(marketName.replace("[", "").replace("]", "").trim());

                    String title = str.replaceFirst("\\[([^\\]]+)\\]", "").trim();
                    dto.setTitle(title);
                }

                // String price = tr.select("span.text-orange").text();
                // dto.setPrice(price.replace("￦", "").replace("(KRW)", "").trim());

                // dto.setShippingFee(tr.select("span:contains(배송비)").text().replace("배송비",
                // "").trim());
                Long likeCnt;

                if (tr.select("td.recomd").text().equals(""))
                    likeCnt = Long.parseLong("0");
                else
                    likeCnt = Long.parseLong(tr.select("td.recomd").text());

                dto.setLikeCnt(likeCnt);
                dto.setUrl(tr.select("a.deco").attr("href"));
                dto.setTime(times.get(i).text());
                list.add(dto);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }
}