package com.daoutech.api.domain.statistics.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.util.ObjectUtils;

import com.daoutech.api.domain.statistics.dto.StatisticsDto;
import com.daoutech.api.exception.ServiceError;
import com.daoutech.api.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileParser {
	private final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
	private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");
	
	private static final String DELEMETER = "\\|";
	private static final String DELEMETER_TAB = "\\t";	//탭으로 구분시 사용
	private static final String DELEMETER_COMMA = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";	//콤마로 구분시 사용
	
	private static final int NORMAL_COL_SIZE = 6;
	private static final int DATE_HOUR_INDEX = 0;
	private static final int JOIN_CNT_INDEX = 1;
	private static final int LEAVE_CNT_INDEX = 2;
	private static final int PAY_AMOUNT_INDEX = 3;
	private static final int USE_AMOUNT_INDEX = 4;
	private static final int SALES_AMOUNT_INDEX = 5;
	
	public static void writeDataTo(String filePath) throws ServiceException {
		String dateHour = DATETIME_FORMATTER.format(LocalDateTime.now());
		int joinCnt = new Random().nextInt(1000);
		int leaveCnt = new Random().nextInt(100);
		String payAmount = DECIMAL_FORMAT.format((long)Math.abs(new Random().nextInt()));
		String useAmount = DECIMAL_FORMAT.format((long)Math.abs(new Random().nextInt()));
		String salesAmount = DECIMAL_FORMAT.format((long)Math.abs(new Random().nextInt()));
		
		String line = new StringBuilder(dateHour)
				.append("|")
				.append(joinCnt)
				.append("|")
				.append(leaveCnt)
				.append("|")
				.append(payAmount)
				.append("|")
				.append(useAmount)
				.append("|")
				.append(salesAmount)
				.append("\n")
				.toString();
		log.info("line data: {}", line);
		
		try {
			Files.write(Paths.get(filePath), line.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			throw new ServiceException(ServiceError.INTERNAL_ERROR, e.getMessage());
		}
	}

	public static List<StatisticsDto> readDataFrom(String filePath) throws ServiceException {
		List<StatisticsDto> statisticsDtos = new ArrayList<>();
		List<String[]> lines = new ArrayList<>();
		
		if(!Paths.get(filePath).toFile().exists()) {
			throw new ServiceException(ServiceError.NOTFOUND_DATA, filePath);
		}
		
		try {
			lines = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)
					.filter(t -> !ObjectUtils.isEmpty(t))
					.map(t -> t.split(DELEMETER))
					.collect(Collectors.toList());
			
			for(String[] line : lines) {
				if(line.length != NORMAL_COL_SIZE) {
					new ServiceException(ServiceError.INTERNAL_ERROR, "데이터가 정확하지 않습니다.");
				}
				statisticsDtos.add(StatisticsDto.builder()
						.dateHour(line[DATE_HOUR_INDEX].replaceAll("[-\\s]", ""))
						.joinCnt(Integer.valueOf(line[JOIN_CNT_INDEX]))
						.leaveCnt(Integer.valueOf(line[LEAVE_CNT_INDEX]))
						.payAmount(Long.valueOf(line[PAY_AMOUNT_INDEX].replaceAll("[\",]", "")))
						.useAmount(Long.valueOf(line[USE_AMOUNT_INDEX].replaceAll("[\",]", "")))
						.salesAmount(Long.valueOf(line[SALES_AMOUNT_INDEX].replaceAll("[\",]", "")))
						.build());
			}
		} catch (ServiceException e) {
			throw e;
		} catch (IOException e) {
			throw new ServiceException(ServiceError.INTERNAL_ERROR, e.getMessage());
		} catch (Exception e) {
			throw new ServiceException(ServiceError.INTERNAL_ERROR, e.getMessage());
		} finally {
			try {
				Files.delete(Paths.get(filePath));
			} catch (Exception e2) {
			}
		}
		
		return statisticsDtos;
	}
}
