package com.daoutech.api.domain.statistics.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daoutech.api.domain.statistics.dto.StatisticsDto;
import com.daoutech.api.domain.statistics.dto.StatisticsDtoWrapper;
import com.daoutech.api.domain.statistics.entity.Counts;
import com.daoutech.api.domain.statistics.entity.Statistics;
import com.daoutech.api.domain.statistics.repository.StatisticsRepository;
import com.daoutech.api.domain.statistics.support.FileParser;
import com.daoutech.api.exception.ServiceError;
import com.daoutech.api.exception.ServiceException;
import com.daoutech.api.util.DateTimeUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StatisticsService {

	private final StatisticsRepository statisticsRepository;
	
	@Transactional(readOnly = true)
	public StatisticsDto inquiry(String dateHour) throws ServiceException {
		Statistics statistics = statisticsRepository.findById(dateHour)
				.orElseThrow(() -> new ServiceException(ServiceError.NOTFOUND_DATA, "해당 일시의 데이터가 없습니다."));
		
		return StatisticsDto.from(statistics);
	}
	
	@Transactional(readOnly = true)
	public StatisticsDto inquirySumByPeriod(String from, String to) throws ServiceException {
		Counts counts = statisticsRepository.findSumByPeroid(from, to);
		Optional.ofNullable(counts.getJoinCnt())
				.orElseThrow(() -> new ServiceException(ServiceError.NOTFOUND_DATA, "해당 기간의 데이터가 없습니다."));
		
		return StatisticsDto.from(counts);
	}
	
	public StatisticsDto inquirySumByDate(String date) throws ServiceException {
		return this.inquirySumByPeriod(
				DateTimeUtil.toStartDateStringFrom(date), DateTimeUtil.toEndDateStringFrom(date));
	}
	
	@Transactional(readOnly = true)
	public StatisticsDtoWrapper inquiryList(String from, String to, Pageable pageable) throws ServiceException {
		Page<Statistics> page = statisticsRepository.findAllByPeroid(from, to, pageable);
		if(page.getContent().size() == 0) {
			throw new ServiceException(ServiceError.NOTFOUND_DATA, "해당 기간의 데이터가 없습니다.");
		}
		
		return StatisticsDtoWrapper.from(page);
	}
	
	@Transactional
	public StatisticsDto regist(StatisticsDto requestDto) throws ServiceException {
		statisticsRepository.findById(requestDto.getDateHour())
			.ifPresent(o -> {
				throw new ServiceException(ServiceError.CONFLICT_DATA, "해당 일시의 데이터가 이미 등록되어 있습니다.");
			});
		
		return StatisticsDto.from(statisticsRepository.save(requestDto.toEntity()));
	}
	
	@Transactional
	public List<StatisticsDto> registAll(List<StatisticsDto> requestDtos) throws ServiceException {
		
		List<Statistics> statisticses = requestDtos.stream()
				.map(t -> t.toEntity())
				.collect(Collectors.toList());
		
		return StatisticsDto.from(statisticsRepository.saveAll(statisticses));
	}
	
	@Transactional
	public StatisticsDto modify(StatisticsDto requestDto) throws ServiceException {
		Statistics statistics = statisticsRepository.findById(requestDto.getDateHour())
				.orElseThrow(() -> new ServiceException(ServiceError.NOTFOUND_DATA, "해당 일시의 데이터가 없습니다."));
		
		statistics.setJoinCnt(requestDto.getJoinCnt());
		statistics.setLeaveCnt(requestDto.getLeaveCnt());
		statistics.setPayAmount(requestDto.getPayAmount());
		statistics.setUseAmount(requestDto.getUseAmount());
		statistics.setSalesAmount(requestDto.getSalesAmount());
		
		return StatisticsDto.from(statistics);
	}
	
	@Transactional
	public void delete(String dateHour) throws ServiceException {
		Statistics statistics = statisticsRepository.findById(dateHour)
				.orElseThrow(() -> new ServiceException(ServiceError.NOTFOUND_DATA, "해당 일시의 데이터가 없습니다."));
		
		statisticsRepository.deleteByDateHour(statistics.getDateHour());
	}
	
	public void makeNWriteDataTo(String filePath) throws ServiceException {
		FileParser.writeDataTo(filePath);
	}
	
	public List<StatisticsDto> readNRegistFrom(String filePath) throws ServiceException {
		return this.registAll(FileParser.readDataFrom(filePath));
	}
}
