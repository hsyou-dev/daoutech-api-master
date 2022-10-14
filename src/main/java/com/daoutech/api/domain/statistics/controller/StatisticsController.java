package com.daoutech.api.domain.statistics.controller;

import javax.validation.constraints.NotEmpty;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.daoutech.api.domain.statistics.dto.StatisticsDto;
import com.daoutech.api.domain.statistics.dto.StatisticsDtoWrapper;
import com.daoutech.api.domain.statistics.service.StatisticsService;
import com.daoutech.api.domain.statistics.support.StatisticsValidGroups.updateValidGroup;
import com.daoutech.api.support.PrintTimeLog;
import com.daoutech.api.util.ApiResponse;
import com.daoutech.api.util.ApiResponse.ApiResult;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;

@RateLimiter(name = "statisticsApiLimiter")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stat")
public class StatisticsController {
	
	private final StatisticsService statisticsService;
	
	@PrintTimeLog
	@GetMapping("/inquiry")
	public ResponseEntity<ApiResult<StatisticsDto>> inquiry(@RequestParam("dateHour") @NotEmpty(message = "dateHour") String dateHour) {
		return new ResponseEntity<>(ApiResponse.success(statisticsService.inquiry(dateHour)), HttpStatus.OK);
	}
	
	@PrintTimeLog
	@GetMapping("/inquiry/sumOfPeriod")
	public ResponseEntity<ApiResult<StatisticsDto>> inquirySumByPeriod(@RequestParam("from") @NotEmpty(message = "from") String from,
			@RequestParam("to") @NotEmpty(message = "to") String to) {
		return new ResponseEntity<>(ApiResponse.success(statisticsService.inquirySumByPeriod(from, to)), HttpStatus.OK);
	}
	
	@PrintTimeLog
	@GetMapping("/inquiry/sumOfDate")
	public ResponseEntity<ApiResult<StatisticsDto>> inquirySumByDate(@RequestParam("date") @NotEmpty(message = "date") String date) {
		return new ResponseEntity<>(ApiResponse.success(statisticsService.inquirySumByDate(date)), HttpStatus.OK);
	}
	
	@PrintTimeLog
	@GetMapping("/inquiry/list")
	public ResponseEntity<ApiResult<StatisticsDtoWrapper>> inquiryListByPeroid(@RequestParam("from") @NotEmpty(message = "from") String from,
			@RequestParam("to") @NotEmpty(message = "to") String to,
			@PageableDefault(size = 10, sort = {"dateHour"}, direction = Sort.Direction.DESC) Pageable pageable) {
		return new ResponseEntity<>(ApiResponse.success(statisticsService.inquiryList(from, to, pageable)), HttpStatus.OK);
	}
	
	@PrintTimeLog
	@PostMapping("/regist")
	public ResponseEntity<ApiResult<StatisticsDto>> regist(@RequestBody @Validated(
			updateValidGroup.class) StatisticsDto statDto) {
		return new ResponseEntity<>(ApiResponse.success(statisticsService.regist(statDto)), HttpStatus.OK);
	}
	
	@PrintTimeLog
	@PutMapping("/modify")
	public ResponseEntity<ApiResult<StatisticsDto>> modify(@RequestBody @Validated(
			updateValidGroup.class) StatisticsDto statDto) {
		return new ResponseEntity<>(ApiResponse.success(statisticsService.modify(statDto)), HttpStatus.OK);
	}
	
	@PrintTimeLog
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResult<Void>> delete(@RequestParam("dateHour") @NotEmpty(message = "dateHour") String dateHour) {
		statisticsService.delete(dateHour);
		return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
	}
	
}
