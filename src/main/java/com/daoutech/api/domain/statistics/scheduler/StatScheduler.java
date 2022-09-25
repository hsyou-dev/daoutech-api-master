package com.daoutech.api.domain.statistics.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.daoutech.api.domain.statistics.service.StatisticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
public class StatScheduler {

	private final StatisticsService statisticsService;
	
	@Value("${app.statistics-file-path}")
	private String statisticsFilePath;
	
	@Scheduled(cron = "${app.statistics-write-cron}")
	public void writeStatistics() throws Exception {
		statisticsService.makeNWriteDataTo(statisticsFilePath);
	}
	
	@Scheduled(cron = "${app.statistics-read-cron}")
	public void readStatistics() throws Exception {
		statisticsService.readNRegistFrom(statisticsFilePath);
	}
}
