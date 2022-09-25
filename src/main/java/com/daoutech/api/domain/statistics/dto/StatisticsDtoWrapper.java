package com.daoutech.api.domain.statistics.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.daoutech.api.domain.statistics.entity.Statistics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsDtoWrapper {

	private List<StatisticsDto> contents;
	private Pagination paging;
	
	public static StatisticsDtoWrapper from(Page<Statistics> pages) {
		return StatisticsDtoWrapper.builder()
				.contents(StatisticsDto.from(pages.getContent()))
				.paging(Pagination.builder()
						.size(pages.getSize())
						.number(pages.getNumber())
						.totalPages(pages.getTotalPages())
						.totalElements(pages.getTotalElements())
						.numberOfElements(pages.getNumberOfElements())
						.build())
				.build();
	}
}
