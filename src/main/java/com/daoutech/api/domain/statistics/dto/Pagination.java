package com.daoutech.api.domain.statistics.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pagination {
	
	private int size;				//한 페이지당 보여줄 최대갯수
	private int number;				//현재 페이지 번호
	private int totalPages;			//총 페이지 수
	private long totalElements;		//총 아이템 갯수
	private int numberOfElements;	//현재 페이지의 아이템 갯수
	
}
