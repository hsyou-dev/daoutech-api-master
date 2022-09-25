package com.daoutech.api.domain.statistics.dto;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.daoutech.api.domain.statistics.entity.Counts;
import com.daoutech.api.domain.statistics.entity.Statistics;
import com.daoutech.api.domain.statistics.support.StatisticsValidGroups.updateValidGroup;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsDto {
	
	@NotEmpty(groups = updateValidGroup.class, message = "dateHour")
	private String dateHour;
	@NotNull(groups = updateValidGroup.class, message = "joinCnt")
	private Integer joinCnt;
	@NotNull(groups = updateValidGroup.class, message = "leaveCnt")
	private Integer leaveCnt;
	@NotNull(groups = updateValidGroup.class, message = "payAmount")
	private Long payAmount;
	@NotNull(groups = updateValidGroup.class, message = "useAmount")
	private Long useAmount;
	@NotNull(groups = updateValidGroup.class, message = "salesAmount")
	private Long salesAmount;

	public static StatisticsDto from(Statistics statistics) {
		return StatisticsDto.builder()
				.dateHour(statistics.getDateHour())
				.joinCnt(statistics.getJoinCnt())
				.leaveCnt(statistics.getLeaveCnt())
				.payAmount(statistics.getPayAmount())
				.useAmount(statistics.getUseAmount())
				.salesAmount(statistics.getSalesAmount())
				.build();
	}
	
	public static List<StatisticsDto> from(List<Statistics> statisticses) {
		return statisticses.stream()
				.map(StatisticsDto::from)
				.collect(Collectors.toList());
	}
	
	public static StatisticsDto from(Counts counts) {
		return StatisticsDto.builder()
				.joinCnt(counts.getJoinCnt())
				.leaveCnt(counts.getLeaveCnt())
				.payAmount(counts.getPayAmount())
				.useAmount(counts.getUseAmount())
				.salesAmount(counts.getSalesAmount())
				.build();
	}
	
	public Statistics toEntity() {
		return Statistics.builder()
				.dateHour(dateHour)
				.joinCnt(joinCnt)
				.leaveCnt(leaveCnt)
				.payAmount(payAmount)
				.useAmount(useAmount)
				.salesAmount(salesAmount)
				.build();
	}
}
