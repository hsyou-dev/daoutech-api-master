package com.daoutech.api.domain.statistics.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.daoutech.api.domain.statistics.dto.Pagination;
import com.daoutech.api.domain.statistics.dto.StatisticsDto;
import com.daoutech.api.domain.statistics.dto.StatisticsDtoWrapper;
import com.daoutech.api.domain.statistics.service.StatisticsService;
import com.daoutech.api.exception.ServiceError;
import com.daoutech.api.exception.ServiceExceptionHandler;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

	@Mock
	private StatisticsService statisticsService;
	@InjectMocks
	private StatisticsController statisticsController;
	private MockMvc mockMvc;
	
	@BeforeEach
	void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(statisticsController)
				.setControllerAdvice(new ServiceExceptionHandler())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.build();
	}
	
	private static Stream<StatisticsDto> dummyDto() {
		return Stream.of(StatisticsDto.builder()
				.dateHour("1970010100")
				.joinCnt(new Random().nextInt(100))
				.leaveCnt(new Random().nextInt(100))
				.payAmount(Math.abs(new Random().nextLong()))
				.useAmount(Math.abs(new Random().nextLong()))
				.salesAmount(Math.abs(new Random().nextLong()))
				.build());
	}
	
	private static Stream<StatisticsDtoWrapper> dummyWrapperDto() {
		List<StatisticsDto> statDtos = new ArrayList<>();
		int dummySize = 5;
		
		for(int i=0; i<dummySize; i++) {
			statDtos.add(StatisticsDto.builder()
					.joinCnt(new Random().nextInt(100))
					.leaveCnt(new Random().nextInt(100))
					.payAmount(Math.abs(new Random().nextLong()))
					.useAmount(Math.abs(new Random().nextLong()))
					.salesAmount(Math.abs(new Random().nextLong()))
					.build());
		}
		
		return Stream.of(StatisticsDtoWrapper.builder()
				.contents(statDtos)
				.paging(Pagination.builder()
						.size(10)
						.number(0)
						.totalPages(1)
						.totalElements(9)
						.numberOfElements(statDtos.size())
						.build())
				.build());
	}

	/**
	 * **************************************Inquiry************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("해당 시간의 통계 정보 조회 API")
	void inquiry(StatisticsDto dummyDto) throws Exception {
		// given
		when(statisticsService.inquiry(any())).thenReturn(dummyDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry")
				.param("dateHour", dummyDto.getDateHour())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.joinCnt", is(dummyDto.getJoinCnt())))
				.andExpect(jsonPath("$.data.leaveCnt", is(dummyDto.getLeaveCnt())))
				.andExpect(jsonPath("$.data.payAmount", is(dummyDto.getPayAmount())))
				.andExpect(jsonPath("$.data.useAmount", is(dummyDto.getUseAmount())))
				.andExpect(jsonPath("$.data.salesAmount", is(dummyDto.getSalesAmount())))
				.andDo(print())
				.andReturn();
	}
	
	@Test
	@DisplayName("해당 시간의 통계 정보 조회 API - 필수 값이 유효하지 않은 경우")
	void inquiryFail_EmptyParam() throws Exception {
		// given
		String dateHour = null;
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry")
				.param("dateHour", dateHour)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("해당 기간의 통계 합산 정보 조회 API")
	void inquirySumOfPeroid(StatisticsDto dummyDto) throws Exception {
		// given
		String from = "1970010100";
		String to = "1970010110";
		when(statisticsService.inquirySumByPeriod(any(), any())).thenReturn(dummyDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry/sumOfPeriod")
				.param("from", from)
				.param("to", to)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.joinCnt", is(dummyDto.getJoinCnt())))
				.andExpect(jsonPath("$.data.leaveCnt", is(dummyDto.getLeaveCnt())))
				.andExpect(jsonPath("$.data.payAmount", is(dummyDto.getPayAmount())))
				.andExpect(jsonPath("$.data.useAmount", is(dummyDto.getUseAmount())))
				.andExpect(jsonPath("$.data.salesAmount", is(dummyDto.getSalesAmount())))
				.andDo(print())
				.andReturn();
	}

	@Test
	@DisplayName("해당 기간의 통계 합산 정보 조회 API - 필수 값이 유효하지 않은 경우")
	void inquirySumOfPeroidFail_EmptyParam() throws Exception {
		// given
		String from = null;
		String to = null;
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry/sumOfPeriod")
				.param("from", from)
				.param("to", to)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("해당 일자의 통계 합산 정보 조회 API")
	void inquirySumOfDate(StatisticsDto dummyDto) throws Exception {
		// given
		String date = "19700101";
		when(statisticsService.inquirySumByDate(any())).thenReturn(dummyDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry/sumOfDate")
				.param("date", date)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.joinCnt", is(dummyDto.getJoinCnt())))
				.andExpect(jsonPath("$.data.leaveCnt", is(dummyDto.getLeaveCnt())))
				.andExpect(jsonPath("$.data.payAmount", is(dummyDto.getPayAmount())))
				.andExpect(jsonPath("$.data.useAmount", is(dummyDto.getUseAmount())))
				.andExpect(jsonPath("$.data.salesAmount", is(dummyDto.getSalesAmount())))
				.andDo(print())
				.andReturn();
	}
	
	@Test
	@DisplayName("해당 일자의 통계 합산 정보 조회 API - 필수 값이 유효하지 않은 경우")
	void inquirySumOfDateFail_EmptyParam() throws Exception {
		// given
		String date = null;
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry/sumOfDate")
				.param("date", date)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
		.andExpect(status().is4xxClientError())
		.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
		.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
		.andDo(print())
		.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyWrapperDto")
	@DisplayName("해당 기간의 통계 정보 리스트 조회 API")
	void inquiryList(StatisticsDtoWrapper dummyWrapperDto) throws Exception {
		log.info(dummyWrapperDto.toString());
		// given
		String from = "1970010100";
		String to = "1970010110";
		when(statisticsService.inquiryList(any(), any(), any())).thenReturn(dummyWrapperDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry/list")
				.param("from", from)
				.param("to", to)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.contents.length()", is(dummyWrapperDto.getContents().size())))
				.andExpect(jsonPath("$.data.paging.numberOfElements", is(dummyWrapperDto.getContents().size())))
				.andDo(print())
				.andReturn();
	}
	
	@Test
	@DisplayName("해당 기간의 통계 정보 리스트 조회 API - 필수 값이 유효하지 않은 경우")
	void inquiryListFail_EmptyParam() throws Exception {
		// given
		String from = null;
		String to = null;
		
		// when
		ResultActions resultActions = mockMvc.perform(get("/api/v1/stat/inquiry/list")
				.param("from", from)
				.param("to", to)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}
	
	
	/**
	 * **************************************Regist************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 등록 API")
	void regist(StatisticsDto dummyDto) throws Exception {
		// given
		when(statisticsService.regist(any())).thenReturn(dummyDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/stat/regist")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.joinCnt", is(dummyDto.getJoinCnt())))
				.andExpect(jsonPath("$.data.leaveCnt", is(dummyDto.getLeaveCnt())))
				.andExpect(jsonPath("$.data.payAmount", is(dummyDto.getPayAmount())))
				.andExpect(jsonPath("$.data.useAmount", is(dummyDto.getUseAmount())))
				.andExpect(jsonPath("$.data.salesAmount", is(dummyDto.getSalesAmount())))
				.andDo(print())
				.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 등록 API - 필수 값이 유효하지 않은 경우")
	void regist_EmptyParam(StatisticsDto dummyDto) throws Exception {
		// given
		dummyDto.setDateHour(null);
		
		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/stat/regist")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}
	
	
	/**
	 * **************************************Modify************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 수정 API")
	void modify(StatisticsDto dummyDto) throws Exception {
		// given
		when(statisticsService.modify(any())).thenReturn(dummyDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(put("/api/v1/stat/modify")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.joinCnt", is(dummyDto.getJoinCnt())))
				.andExpect(jsonPath("$.data.leaveCnt", is(dummyDto.getLeaveCnt())))
				.andExpect(jsonPath("$.data.payAmount", is(dummyDto.getPayAmount())))
				.andExpect(jsonPath("$.data.useAmount", is(dummyDto.getUseAmount())))
				.andExpect(jsonPath("$.data.salesAmount", is(dummyDto.getSalesAmount())))
				.andDo(print())
				.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 수정 API - 필수 값이 유효하지 않은 경우")
	void modify_EmptyParam(StatisticsDto dummyDto) throws Exception {
		// given
		dummyDto.setDateHour(null);
		
		// when
		ResultActions resultActions = mockMvc.perform(put("/api/v1/stat/modify")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}
	
	
	/**
	 * **************************************Delete************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 삭제 API")
	void deleteStat(StatisticsDto dummyDto) throws Exception {
		// given
		// dummyDto
		
		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/v1/stat/delete")
				.param("dateHour", dummyDto.getDateHour())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
		.andDo(print())
		.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 삭제 API - 필수 값이 유효하지 않은 경우")
	void deleteStat_EmptyParam(StatisticsDto dummyDto) throws Exception {
		// given
		dummyDto.setDateHour(null);
		
		// when
		ResultActions resultActions = mockMvc.perform(delete("/api/v1/stat/delete")
				.param("dateHour", dummyDto.getDateHour())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}
}
