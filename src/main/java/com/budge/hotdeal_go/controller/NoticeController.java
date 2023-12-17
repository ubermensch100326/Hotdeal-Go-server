package com.budge.hotdeal_go.controller;

import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.budge.hotdeal_go.exception.NoticeException;
import com.budge.hotdeal_go.model.dto.MemberDto;
import com.budge.hotdeal_go.model.dto.MessageDto;
import com.budge.hotdeal_go.model.dto.ModifyNoticeDto;
import com.budge.hotdeal_go.model.dto.NoticeDetailDto;
import com.budge.hotdeal_go.model.dto.NoticeListDto;
import com.budge.hotdeal_go.model.dto.NoticeParamDto;
import com.budge.hotdeal_go.model.dto.WriteDto;
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
	
	@ApiOperation(value = "공지사항 목록", notes = "공지사항 글 정보 반환", response = List.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "공지사항 목록 OK!"), @ApiResponse(code = 404, message = "해당 페이지 존재 X!"),
		@ApiResponse(code = 500, message = "내부 서버 오류!")
	})
	@GetMapping
	public ResponseEntity<NoticeListDto> getNoticeList(@RequestParam(required = false, defaultValue = "") @ApiParam(value = "키워드") String keyword, @RequestParam(required = false, defaultValue = "1") @ApiParam(value = "현재 페이지 번호") int pgno, @RequestParam(required = false, defaultValue = "20") @ApiParam(value = "페이지당 글의 수") int npp) {
		NoticeParamDto noticeParamDto = new NoticeParamDto();
		noticeParamDto.setKeyword(keyword);
		noticeParamDto.setPgno(pgno);
		noticeParamDto.setNpp(npp);
		log.info("listNotice noticeParamDto - {}", noticeParamDto);
		try {
			NoticeListDto noticeListDto = noticeService.getNoticeList(noticeParamDto);
			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
			return ResponseEntity.ok().headers(header).body(noticeListDto);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoticeException();
		}
	}

	@ApiOperation(value = "공지사항 글작성", notes = "새로운 공지사항 작성", response = MessageDto.class)
	@PostMapping
	public ResponseEntity<MessageDto> writeNotice(
			@RequestBody @ApiParam(value = "게시글 정보.", required = true) WriteDto writeDto, HttpServletRequest request) {
		log.info("writeBoard writeDto - {}", writeDto);
		MemberDto memberDto = (MemberDto) request.getAttribute("memberFind");
		NoticeDetailDto noticeDetailDto = NoticeDetailDto.builder()
				.title(writeDto.getTitle())
				.content(writeDto.getContent())
				.memberNo(memberDto.getNo())
				.build();
		
		try {
			noticeService.writeNotice(noticeDetailDto);
			MessageDto messageDto = MessageDto.builder()
					.message("공지사항 작성 완료")
					.build();
			return new ResponseEntity<MessageDto>(messageDto, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoticeException();
		}
	}
	
	@ApiOperation(value = "공지사항 글보기", notes = "글번호에 해당하는 공지사항의 세부내용 반환", response = NoticeDetailDto.class)
	@GetMapping("/{noticeno}")
	public ResponseEntity<NoticeDetailDto> getNoticeDetail(@PathVariable("noticeno") @ApiParam(value = "얻어올 공지사항의 글번호", required = true) int noticeno) {
		log.info("getNoticeDetail noticeNo - {}", noticeno);
		noticeService.updateHit(noticeno);
		return new ResponseEntity<NoticeDetailDto>(noticeService.getNoticeDetail(noticeno), HttpStatus.OK);
	}
	
//	인터셉터 어떻게 적용할지 고민할 것
//	일단은 프론트 쪽에서 수정 버튼이 안 보일 거라서 상관없을 듯
	@ApiOperation(value = "공지사항 수정글 얻기", notes = "글번호에 해당하는 수정할 공지사항의 세부내용 반환", response = NoticeDetailDto.class)
	@GetMapping("/modify/{noticeno}")
	public ResponseEntity<NoticeDetailDto> getModifyNoticeDetail(@PathVariable("noticeno") @ApiParam(value = "얻어올 공지사항의 글번호", required = true) int noticeno) {
		log.info("getModifyNoticeDetail - 호출 - {}" + noticeno);
		return new ResponseEntity<NoticeDetailDto>(noticeService.getNoticeDetail(noticeno), HttpStatus.OK);
	}
	
	@ApiOperation(value = "공지사항 수정", notes = "수정할 공지사항 정보를 입력한 뒤 수정 클릭", response = MessageDto.class)
	@PutMapping
	public ResponseEntity<MessageDto> modifyNoticeDetail(@RequestBody @ApiParam(value = "수정할 공지사항 정보", required = true) ModifyNoticeDto modifyNoticeDto) {
		log.info("modifyNoticeDetail 호출 - {}", modifyNoticeDto);
		try {
			noticeService.modifyNoticeDetail(modifyNoticeDto);
			MessageDto messageDto = MessageDto.builder()
					.message("공지사항 수정 완료")
					.build();
			return new ResponseEntity<MessageDto>(messageDto, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoticeException();
		}
	}
	
//	삭제할 때 유저정보 삭제해야되는지 알아볼 것
	@ApiOperation(value = "공지사항 삭제", notes = "글번호에 해당하는 공지사항 삭제", response = MessageDto.class)
	@DeleteMapping("/{noticeno}")
	public ResponseEntity<MessageDto> deleteNoticeDetail(@PathVariable("noticeno") @ApiParam(value = "삭제할 공지사항의 글번호", required = true) int noticeno) {
		log.info("deleteNoticeDetail 호출 - {}", noticeno);
		try {
			noticeService.deleteNoticeDetail(noticeno);
			MessageDto messageDto = MessageDto.builder()
					.message("공지사항 삭제 완료")
					.build();
			return new ResponseEntity<MessageDto>(messageDto, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NoticeException();
		}
	}
	
	
}
