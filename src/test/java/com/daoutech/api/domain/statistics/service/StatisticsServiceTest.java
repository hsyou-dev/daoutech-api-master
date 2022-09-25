package com.daoutech.api.domain.statistics.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.daoutech.api.domain.statistics.dto.StatisticsDto;
import com.daoutech.api.domain.statistics.dto.StatisticsDtoWrapper;
import com.daoutech.api.domain.statistics.entity.Counts;
import com.daoutech.api.domain.statistics.entity.Statistics;
import com.daoutech.api.domain.statistics.repository.StatisticsRepository;
import com.daoutech.api.exception.ServiceException;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {
	
	@Mock
	private StatisticsRepository statisticsRepository;
	@InjectMocks
	private StatisticsService statisticsService;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	/**
	 * **************************************Dummy************************************** 
	 */
	private static Stream<StatisticsDto> dummyDto() {
		return Stream.of(StatisticsDto.builder()
				.dateHour("1970010100")
				.joinCnt(new Random().nextInt(100))
				.leaveCnt(new Random().nextInt(100))
				.payAmount((long)Math.abs(new Random().nextInt()))
				.useAmount((long)Math.abs(new Random().nextInt()))
				.salesAmount((long)Math.abs(new Random().nextInt()))
				.build());
	}
	
	private static Stream<List<StatisticsDto>> dummyDtos() {
		List<StatisticsDto> statDtos = new ArrayList<>();
		int dummySize = 24;
		
		for(int i=0; i<dummySize; i++) {
			statDtos.add(StatisticsDto.builder()
					.dateHour("19700101" + String.format("%02d", i))
					.joinCnt(new Random().nextInt(100))
					.leaveCnt(new Random().nextInt(100))
					.payAmount((long)Math.abs(new Random().nextInt()))
					.useAmount((long)Math.abs(new Random().nextInt()))
					.salesAmount((long)Math.abs(new Random().nextInt()))
					.build());
		}
		
		return Stream.of(statDtos);
	}
	
	private static Stream<Page<Statistics>> dummyPage() {
		List<Statistics> stats = new ArrayList<>();
		int dummySize = 5;
		
		for(int i=0; i<dummySize; i++) {
			stats.add(Statistics.builder()
					.joinCnt(new Random().nextInt(100))
					.leaveCnt(new Random().nextInt(100))
					.payAmount((long)Math.abs(new Random().nextInt()))
					.useAmount((long)Math.abs(new Random().nextInt()))
					.salesAmount((long)Math.abs(new Random().nextInt()))
					.build());
		}
		
		return Stream.of(new PageImpl<>(stats));
	}
	
	private static Stream<Page<Statistics>> emytyDummyPage() {
		List<Statistics> stats = new ArrayList<>();
		return Stream.of(new PageImpl<>(stats));
	}
	
	private static Stream<Counts> dummyCounts() {
		return Stream.of(new Counts() {
			@Override
			public Long getUseAmount() {return (long)Math.abs(new Random().nextInt());}
			@Override
			public Long getSalesAmount() {return (long)Math.abs(new Random().nextInt());}
			@Override
			public Long getPayAmount() {return (long)Math.abs(new Random().nextInt());}
			@Override
			public Integer getLeaveCnt() {return new Random().nextInt(100);}
			@Override
			public Integer getJoinCnt() {return new Random().nextInt(100);}
		});
	}
	
	private static Stream<Counts> emptyDummyCounts() {
		return Stream.of(new Counts() {
			@Override
			public Long getUseAmount() {return null;}
			@Override
			public Long getSalesAmount() {return null;}
			@Override
			public Long getPayAmount() {return null;}
			@Override
			public Integer getLeaveCnt() {return null;}
			@Override
			public Integer getJoinCnt() {return null;}
		});
	}

	
	/**
	 * **************************************Inquiry************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("해당 시간의 통계 정보 조회")
	void inquirySuccess(StatisticsDto dummyDto) throws Exception {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.of(dummyDto.toEntity()));
		
		// when
		StatisticsDto resultDto = statisticsService.inquiry(dummyDto.getDateHour());
		
		// then
		assertNotNull(resultDto);
		assertEquals(dummyDto.getDateHour(), resultDto.getDateHour());
		assertNotNull(resultDto.getJoinCnt());
		assertNotNull(resultDto.getLeaveCnt());
		assertNotNull(resultDto.getPayAmount());
		assertNotNull(resultDto.getUseAmount());
		assertNotNull(resultDto.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("해당 시간의 통계 정보 조회 - 데이터가 없는 경우")
	void inquiryFail_emptyData(StatisticsDto dummyDto) throws Exception {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.empty());
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			statisticsService.inquiry(dummyDto.getDateHour());
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.NOT_FOUND, se.getStatus());
	}
	
	@ParameterizedTest
	@MethodSource("dummyCounts")
	@DisplayName("해당 기간의 통계 합산 정보 조회")
	void inquirySumByPeroidSuccess(Counts dummyCounts) throws Exception {
		// given
		String from = "1970010100";
		String to = "1970010110";
		when(statisticsRepository.findSumByPeroid(any(), any())).thenReturn(dummyCounts);
		
		// when
		StatisticsDto resultDto = statisticsService.inquirySumByPeriod(from, to);
		
		// then
		assertNotNull(resultDto);
		assertNotNull(resultDto.getJoinCnt());
		assertNotNull(resultDto.getLeaveCnt());
		assertNotNull(resultDto.getPayAmount());
		assertNotNull(resultDto.getUseAmount());
		assertNotNull(resultDto.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("emptyDummyCounts")
	@DisplayName("해당 기간의 통계 합산 정보 조회 - 데이터가 없는 경우")
	void inquirySumByPeroidFail_emptyData(Counts emptyDummyCounts) throws Exception {
		// given
		String from = "1970010100";
		String to = "1970010110";
		when(statisticsRepository.findSumByPeroid(any(), any())).thenReturn(emptyDummyCounts);
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			statisticsService.inquirySumByPeriod(from, to);
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.NOT_FOUND, se.getStatus());
	}
	
	@ParameterizedTest
	@MethodSource("dummyCounts")
	@DisplayName("해당 일자의 통계 합산 정보 조회")
	void inquirySumByDateSuccess(Counts dummyCounts) throws Exception {
		// given
		String date = "19700101";
		when(statisticsRepository.findSumByPeroid(any(), any())).thenReturn(dummyCounts);
		
		// when
		StatisticsDto resultDto = statisticsService.inquirySumByDate(date);
		
		// then
		assertNotNull(resultDto);
		assertNotNull(resultDto.getJoinCnt());
		assertNotNull(resultDto.getLeaveCnt());
		assertNotNull(resultDto.getPayAmount());
		assertNotNull(resultDto.getUseAmount());
		assertNotNull(resultDto.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("emptyDummyCounts")
	@DisplayName("해당 일자의 통계 합산 정보 조회 - 데이터가 없는 경우")
	void inquirySumByDateFail_emptyData(Counts emptyDummyCounts) throws Exception {
		// given
		String date = "19700101";
		when(statisticsRepository.findSumByPeroid(any(), any())).thenReturn(emptyDummyCounts);
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			statisticsService.inquirySumByDate(date);
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.NOT_FOUND, se.getStatus());
	}
	
	@ParameterizedTest
	@MethodSource("dummyPage")
	@DisplayName("해당 기간의 통계 목록 조회")
	void inquiryListSuccess(Page<Statistics> dummyPage) throws Exception {
		// given
		String from = "1970010100";
		String to = "1970010110";
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateHour"));
		when(statisticsRepository.findAllByPeroid(any(), any(), any())).thenReturn(dummyPage);
		
		// when
		StatisticsDtoWrapper resultDtoWrapper = statisticsService.inquiryList(from, to, pageable);
		
		// then
		assertNotNull(resultDtoWrapper);
		assertEquals(dummyPage.getContent().size(), resultDtoWrapper.getContents().size());
		assertEquals(dummyPage.getContent().size(), resultDtoWrapper.getPaging().getNumberOfElements());
	}
	
	@ParameterizedTest
	@MethodSource("emytyDummyPage")
	@DisplayName("해당 기간의 통계 목록 조회 실패 - 데이터가 없는 경우")
	void inquiryListFail_emptyData(Page<Statistics> emptyDummyPage) throws Exception {
		// given
		String from = "1970010100";
		String to = "1970010110";
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateHour"));
		when(statisticsRepository.findAllByPeroid(any(), any(), any())).thenReturn(emptyDummyPage);
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			statisticsService.inquiryList(from, to, pageable);
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.NOT_FOUND, se.getStatus());
	}
	
	
	/**
	 * **************************************Regist************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 등록 성공 - 동일 일시의 데이터가 등록되어 있지 않은 경우")
	void registSuccess(StatisticsDto dummyDto) {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.empty());
		when(statisticsRepository.save(any())).thenReturn(dummyDto.toEntity());
		
		// when
		StatisticsDto resultDto = statisticsService.regist(dummyDto);
		
		// then
		assertNotNull(resultDto);
		assertEquals(dummyDto.getDateHour(), resultDto.getDateHour());
		assertEquals(dummyDto.getJoinCnt(), resultDto.getJoinCnt());
		assertEquals(dummyDto.getLeaveCnt(), resultDto.getLeaveCnt());
		assertEquals(dummyDto.getPayAmount(), resultDto.getPayAmount());
		assertEquals(dummyDto.getUseAmount(), resultDto.getUseAmount());
		assertEquals(dummyDto.getSalesAmount(), resultDto.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 등록 실패 - 동일 일시의 데이터가 등록되어 있는 경우")
	void registFail_conflictId(StatisticsDto dummyDto) {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.of(dummyDto.toEntity()));
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			statisticsService.regist(dummyDto);
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.CONFLICT, se.getStatus());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDtos")
	@DisplayName("통계 정보 목록 등록")
	void registAllSuccess(List<StatisticsDto> dummyDtos) {
		// given
		when(statisticsRepository.saveAll(any())).thenReturn(dummyDtos.stream()
				.map(t -> t.toEntity())
				.collect(Collectors.toList()));
		
		// when
		List<StatisticsDto> resultDtos = statisticsService.registAll(dummyDtos);
		
		// then
		assertNotNull(resultDtos);
		assertEquals(dummyDtos.size(), resultDtos.size());
		for(int i=0; i<dummyDtos.size(); i++) {
			assertEquals(dummyDtos.get(i).getDateHour(), resultDtos.get(i).getDateHour());
		}
	}
	
	
	/**
	 * **************************************Modify************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 수정 성공 - 동일 일시의 데이터가 등록되어 있는 경우")
	void modifySuccess(StatisticsDto dummyDto) {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.of(dummyDto.toEntity()));
		
		// when
		StatisticsDto resultDto = statisticsService.modify(dummyDto);
		
		// then
		assertNotNull(resultDto);
		assertEquals(dummyDto.getDateHour(), resultDto.getDateHour());
		assertEquals(dummyDto.getJoinCnt(), resultDto.getJoinCnt());
		assertEquals(dummyDto.getLeaveCnt(), resultDto.getLeaveCnt());
		assertEquals(dummyDto.getPayAmount(), resultDto.getPayAmount());
		assertEquals(dummyDto.getUseAmount(), resultDto.getUseAmount());
		assertEquals(dummyDto.getSalesAmount(), resultDto.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 수정 실패 - 동일 일시의 데이터가 등록되어 있지 않은 경우")
	void modifyFail_emptyData(StatisticsDto dummyDto) {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.empty());
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			statisticsService.modify(dummyDto);
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.NOT_FOUND, se.getStatus());
	}
	
	
	/**
	 * **************************************Delete************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 삭제 성공 - 동일 일시의 데이터가 등록되어 있는 경우")
	void deleteSuccess(StatisticsDto dummyDto) {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.of(dummyDto.toEntity()));
		
		// when
		statisticsService.delete(dummyDto.getDateHour());
		
		// then
		//notthing
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 삭제 실패 - 동일 일시의 데이터가 등록되어 있지 않은 경우")
	void deleteFail_emptyData(StatisticsDto dummyDto) {
		// given
		when(statisticsRepository.findById(any())).thenReturn(Optional.empty());
		
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			statisticsService.delete(dummyDto.getDateHour());
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.NOT_FOUND, se.getStatus());
	}
	
}
