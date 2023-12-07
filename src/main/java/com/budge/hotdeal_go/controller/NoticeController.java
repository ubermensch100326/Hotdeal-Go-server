package com.budge.hotdeal_go.controller;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.model.dto.NoticeListDto;
import com.budge.hotdeal_go.model.dto.NoticeParamDto;
import com.budge.hotdeal_go.model.service.NoticeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/notice")
@Api("공지사항 컨트롤러 API V1")
@Slf4j
public class NoticeController {
	
	private NoticeService noticeService;
	
	@Autowired
	public NoticeController(NoticeService noticeService) {
		super();
		this.noticeService = noticeService;
	}
	
	@ApiOperation(value = "공지사항 목록", notes = "공지사항 정보를 반환한다.", response = List.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "공지사항 목록 OK!"), @ApiResponse(code = 404, message = "해당 페이지 존재 X!"),
		@ApiResponse(code = 500, message = "내부 서버 오류!")
	})
	@GetMapping
	public ResponseEntity<?> listNotice(@RequestParam(required = false, defaultValue = "") @ApiParam(value = "키워드") String keyword, @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호") int pgno, @RequestParam(required = false, defaultValue = "20") @ApiParam(value = "페이지당 글의 수") int npp) {
		NoticeParamDto noticeParamDto = new NoticeParamDto();
		noticeParamDto.setKeyword(keyword);
		noticeParamDto.setPgno(pgno);
		noticeParamDto.setNpp(npp);
		log.info("listNotice noticeParamDto - {}", noticeParamDto);
		try {
			NoticeListDto noticeListDto = noticeService.listNotice(noticeParamDto);
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
			return ResponseEntity.ok().headers(header).body(noticeListDto);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}
	
	private ResponseEntity<String> exceptionHandling(Exception e) {
		e.printStackTrace();
		return new ResponseEntity<String>("Error : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
