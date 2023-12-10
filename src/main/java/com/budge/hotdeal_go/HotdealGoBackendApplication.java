package com.budge.hotdeal_go;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.budge.hotdeal_go.model.dto.HotDealDto;
import com.budge.hotdeal_go.model.service.SearchService;

@SpringBootApplication
public class HotdealGoBackendApplication {

	private static SearchService searchService;

	@Autowired
	public HotdealGoBackendApplication(SearchService searchService) {
		super();
		HotdealGoBackendApplication.searchService = searchService;
	}

	public static void main(String[] args) {
		SpringApplication.run(HotdealGoBackendApplication.class, args);

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		// 크롤링 작업을 수행하는 Runnable (쓰레드 추가)
		Runnable crawlingTask = () -> {
			// DB 최신화 작업
			try {
				crawlAndSaveData();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		};

		// 스케줄링: 주기 10분
		// (서버 시작시 1분 지연 후 10분마다 카운팅하여 스스로 최신화)
		scheduler.scheduleAtFixedRate(crawlingTask, 1, 10, TimeUnit.MINUTES);
	}

	private static void crawlAndSaveData() throws SQLException {
		System.out.println("[쓰레드] 설정한 3개의 커뮤니티를 크롤링합니다.");

		List<HotDealDto> fmKorea = getFmKorea();
		List<HotDealDto> quasarZone = getQuasarZone();
		List<HotDealDto> ruliweb = getRuliweb();

		if (fmKorea.size() == 0) {
			System.out.println("[쓰레드] 에펨코리아에서 크롤링하지 못하였습니다.");
		} else {
			System.out.println("[쓰레드] 에펨코리아 -> DB 최신화");
			boolean check1 = searchService.regist(fmKorea);
			if (check1)
				System.out.println("에펨코리아 성공");
			else
				System.out.println("에펨코리아 실패");
		}

		if (quasarZone.size() == 0) {
			System.out.println("[쓰레드] 퀘이사존에서 크롤링하지 못하였습니다.");
		} else {
			System.out.println("[쓰레드] 퀘이사존 -> DB 최신화");
			boolean check2 = searchService.regist(quasarZone);
			if (check2)
				System.out.println("퀘이사존 성공");
			else
				System.out.println("퀘이사존 실패");
		}

		if (ruliweb.size() == 0) {
			System.out.println("[쓰레드] 루리웹에서 크롤링하지 못하였습니다.");
		} else {
			System.out.println("[쓰레드] 루리웹 -> DB 최신화");
			boolean check3 = searchService.regist(ruliweb);
			if (check3)
				System.out.println("루리웹 성공");
			else
				System.out.println("루리웹 실패");
		}
	}

	public static List<HotDealDto> getFmKorea() {
		List<HotDealDto> list = new ArrayList<>();

		String url = "https://www.fmkorea.com/hotdeal";

		try {
			// 해당 URL에서 HTML 가져오기
			Document document = Jsoup.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0")
					.get();

			// 가져온 HTML에서 특정 요소(예: 글 제목) 추출
			Elements titles = document.select("h3.title > a");
			Elements prices = document.select("span:contains(가격)");
			Elements lis = document.select("li.li");
			Elements shippingFees = document.select("span:contains(배송)");
			Elements purchasingPlaces = document.select("span:contains(쇼핑몰)");
			Elements times = document.select("span.regdate");

			// 날짜 형식 추가
			LocalDate currentDate = LocalDate.now();

			// 년, 월, 일 추출
			int year = currentDate.getYear();
			int month = currentDate.getMonthValue();
			int day = currentDate.getDayOfMonth();

			// 추출된 요소 출력
			int i = 0;
			for (Element title : titles) {
				HotDealDto dto = new HotDealDto();
				if (title.text().equals("") || title.text() == null)
					continue;

				String pattern = "\\s*\\[[^\\]]*\\]\\s*";
				Pattern p = Pattern.compile(pattern);
				Matcher m = p.matcher(title.text());
				dto.setTitle(m.replaceAll("").trim());

				dto.setPrice(prices.get(i).selectFirst("a").text().replace("원", "").trim());
				dto.setUrl("https://www.fmkorea.com" + title.attr("href"));
				dto.setShippingFee(shippingFees.get(i).selectFirst("a").text());
				dto.setPurchasingPlace(purchasingPlaces.get(i).selectFirst("a").text());
				dto.setTime(year + "-" + month + "-" + day + " " + times.get(i).text());
				dto.setImg(lis.get(i).select("img.thumb").attr("data-original"));

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
			Document document = Jsoup.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0")
					.get();

			// 가져온 HTML에서 특정 요소(예: 글 제목) 추출
			Elements trs = document.select("tr");
			Elements times = document.select("span.date");

			// 날짜 형식 추가
			LocalDate currentDate = LocalDate.now();

			// 년, 월, 일 추출
			int year = currentDate.getYear();
			int month = currentDate.getMonthValue();
			int day = currentDate.getDayOfMonth();

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
					String title = str.replaceFirst("\\[([^\\]]+)\\]", "").trim();
					if (title.equals("") || title == null)
						continue;
					dto.setTitle(title);

					String marketName = matcher.group(1);
					dto.setPurchasingPlace(marketName.replace("[", "").replace("]", "").trim());
				}

				dto.setTitle(tr.select("span.ellipsis-with-reply-cnt").text());

				String price = tr.select("span.text-orange").text();
				dto.setPrice(price.replace("￦", "").replace("(KRW)", "").trim());

				dto.setShippingFee(tr.select("span:contains(배송비)").text().replace("배송비", "").trim());
				dto.setLikeCnt(Long.parseLong(tr.select("span.num").text()));
				dto.setUrl("https://quasarzone.com" + tr.select("a.subject-link").attr("href"));
				dto.setTime(year + "-" + month + "-" + day + " " + times.get(i - 1).text());

				// 이미지 url 얻기
				pattern = Pattern.compile("url\\((.*?)\\)");
				matcher = pattern.matcher(tr.select("span.img-background-wrap").attr("style"));
				matcher.find();
				dto.setImg(matcher.group(1));

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
			Document document = Jsoup.connect(url)
					.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0")
					.get();

			// 가져온 HTML에서 특정 요소(예: 글 제목) 추출
			Elements trs = document.select("tr.table_body");
			Elements times = document.select("td.time");

			// 날짜 형식 추가
			LocalDate currentDate = LocalDate.now();

			// 년, 월, 일 추출
			int year = currentDate.getYear();
			int month = currentDate.getMonthValue();
			int day = currentDate.getDayOfMonth();

			for (int i = 8; i < trs.size(); i++) {
				HotDealDto dto = new HotDealDto();

				Element tr = trs.get(i);

				String str = tr.select("a.deco").text();

				String regex = "\\[([^\\]]+)\\]";
				Pattern pattern = Pattern.compile(regex);

				Matcher matcher = pattern.matcher(str);
				if (matcher.find()) {
					String title = str.replaceFirst("\\[([^\\]]+)\\]", "").trim();
					if (title.equals("") || title == null)
						continue;

					dto.setTitle(title);

					String marketName = matcher.group(1);
					dto.setPurchasingPlace(marketName.replace("[", "").replace("]", "").trim());
				}
				Long likeCnt;

				if (tr.select("td.recomd").text().equals(""))
					likeCnt = Long.parseLong("0");
				else
					likeCnt = Long.parseLong(tr.select("td.recomd").text());

				dto.setLikeCnt(likeCnt);
				dto.setUrl(tr.select("a.deco").attr("href"));

				String getTime = times.get(i).text();
				if (getTime.contains(":")) {
					dto.setTime(year + "-" + month + "-" + day + " " + getTime);
				} else {
					dto.setTime(getTime);
				}

				list.add(dto);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
}