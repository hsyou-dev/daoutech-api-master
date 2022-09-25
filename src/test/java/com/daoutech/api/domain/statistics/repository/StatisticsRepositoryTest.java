package com.daoutech.api.domain.statistics.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.daoutech.api.domain.statistics.dto.StatisticsDto;
import com.daoutech.api.domain.statistics.entity.Counts;
import com.daoutech.api.domain.statistics.entity.Statistics;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StatisticsRepositoryTest {
	
	@Autowired
	private StatisticsRepository statisticsRepository;

	@BeforeEach
	void setUp() throws Exception {
	}
	
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

	
	/**
	 * **************************************Find************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 등록정보 조회 - 등록정보가 있는 경우")
	void findById(StatisticsDto dummyDto) {
		// given
		statisticsRepository.save(dummyDto.toEntity());
		
		// when
		Optional<Statistics> o = statisticsRepository.findById(dummyDto.getDateHour());
		
		// then
		assertTrue(o.isPresent());
		Statistics resultData = o.get();
		assertEquals(dummyDto.getDateHour(), resultData.getDateHour());
		assertEquals(dummyDto.getJoinCnt(), resultData.getJoinCnt());
		assertEquals(dummyDto.getLeaveCnt(), resultData.getLeaveCnt());
		assertEquals(dummyDto.getPayAmount(), resultData.getPayAmount());
		assertEquals(dummyDto.getUseAmount(), resultData.getUseAmount());
		assertEquals(dummyDto.getSalesAmount(), resultData.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 등록정보 조회 - 등록정보가 없는 경우")
	void findById_emptyData(StatisticsDto dummyDto) {
		// given
		String dateHour = dummyDto.getDateHour();
		
		// when
		Optional<Statistics> o = statisticsRepository.findById(dateHour);
		
		// then
		assertFalse(o.isPresent());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDtos")
	@DisplayName("통계 등록정보 합산 조회 - 등록정보가 있는 경우")
	void findSumByPeroid(List<StatisticsDto> dummyDtos) {
		// given
		String from = dummyDtos.get(0).getDateHour();
		String to = dummyDtos.get(dummyDtos.size()-1).getDateHour();
		statisticsRepository.saveAll(dummyDtos.stream()
				.map(t -> t.toEntity())
				.collect(Collectors.toList()));
		
		// when
		Counts counts = statisticsRepository.findSumByPeroid(from, to);
		
		// then
		assertNotNull(counts);
		assertNotNull(counts.getJoinCnt());
		assertNotNull(counts.getLeaveCnt());
		assertNotNull(counts.getPayAmount());
		assertNotNull(counts.getUseAmount());
		assertNotNull(counts.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDtos")
	@DisplayName("통계 등록정보 합산 조회 - 등록정보가 없는 경우")
	void findSumByPeroid_emptyData(List<StatisticsDto> dummyDtos) {
		// given
		String from = dummyDtos.get(0).getDateHour();
		String to = dummyDtos.get(dummyDtos.size()-1).getDateHour();
		
		// when
		Counts counts = statisticsRepository.findSumByPeroid(from, to);
		
		// then
		assertNotNull(counts);
		assertNull(counts.getJoinCnt());
		assertNull(counts.getLeaveCnt());
		assertNull(counts.getPayAmount());
		assertNull(counts.getUseAmount());
		assertNull(counts.getSalesAmount());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDtos")
	@DisplayName("통계 등록정보 합산 조회 - 등록정보가 있는 경우")
	void findAllByPeroid(List<StatisticsDto> dummyDtos) {
		// given
		Pageable pageable = PageRequest.of(0, dummyDtos.size(), Sort.by(Sort.Direction.ASC, "dateHour"));
		String from = dummyDtos.get(0).getDateHour();
		String to = dummyDtos.get(dummyDtos.size()-1).getDateHour();
		statisticsRepository.saveAll(dummyDtos.stream()
				.map(t -> t.toEntity())
				.collect(Collectors.toList()));
		
		// when
		Page<Statistics> page = statisticsRepository.findAllByPeroid(from, to, pageable);
		
		// then
		assertNotNull(page);
		assertEquals(dummyDtos.size(), page.getContent().size());
		for(int i=0; i<dummyDtos.size(); i++) {
			assertEquals(dummyDtos.get(i).getDateHour(), page.getContent().get(i).getDateHour());
		}
	}
	
	@ParameterizedTest
	@MethodSource("dummyDtos")
	@DisplayName("통계 등록정보 합산 조회 - 등록정보가 없는 경우")
	void findAllByPeroid_emptyData(List<StatisticsDto> dummyDtos) {
		// given
		Pageable pageable = PageRequest.of(0, dummyDtos.size(), Sort.by(Sort.Direction.ASC, "dateHour"));
		String from = dummyDtos.get(0).getDateHour();
		String to = dummyDtos.get(dummyDtos.size()-1).getDateHour();
		
		// when
		Page<Statistics> page = statisticsRepository.findAllByPeroid(from, to, pageable);
		
		// then
		assertNotNull(page);
		assertEquals(0, page.getContent().size());
		assertEquals(0, page.getNumberOfElements());
	}
	
	
	/**
	 * **************************************Save************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 등록")
	void save(StatisticsDto dummyDto) {
		// given
		Statistics stat = dummyDto.toEntity();
		
		// when
		Statistics resultData = statisticsRepository.save(stat);
		
        // then
		assertSame(stat, resultData);
	}
	
	@ParameterizedTest
	@MethodSource("dummyDtos")
	@DisplayName("통계 정보 목록 등록")
	void saveAll(List<StatisticsDto> dummyDtos) {
		// given
		List<Statistics> statList = dummyDtos.stream()
				.map(t -> t.toEntity())
				.collect(Collectors.toList());
		
		// when
		List<Statistics> resultDatas = statisticsRepository.saveAll(statList);
		
		// then
		for(int i=0; i<statList.size(); i++) {
			assertSame(statList.get(i), resultDatas.get(i));
		}
	}
	
	
	/**
	 * **************************************Delete************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("통계 정보 목록 등록")
	void delete(StatisticsDto dummyDto) {
		// given
		statisticsRepository.save(dummyDto.toEntity());
		
		// when
		statisticsRepository.deleteByDateHour(dummyDto.getDateHour());
		
		// then
		Optional<Statistics> o = statisticsRepository.findById(dummyDto.getDateHour());
		assertFalse(o.isPresent());
	}

}
