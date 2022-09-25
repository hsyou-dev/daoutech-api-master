package com.daoutech.api.domain.statistics.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daoutech.api.domain.statistics.entity.Counts;
import com.daoutech.api.domain.statistics.entity.Statistics;

public interface StatisticsRepository extends JpaRepository<Statistics, String> {

	@Query(value = "SELECT stt "
			+ "FROM Statistics stt "
			+ "WHERE stt.dateHour BETWEEN :#{#from} AND :#{#to}"
			, countQuery = "SELECT COUNT(stt) "
					+ "FROM Statistics stt "
					+ "WHERE stt.dateHour BETWEEN :#{#from} AND :#{#to}")
	public Page<Statistics> findAllByPeroid(@Param("from") String from, @Param("to") String to, Pageable pageable);
	
	@Query("SELECT "
				+ "SUM(stt.joinCnt) AS joinCnt, "
				+ "SUM(stt.leaveCnt) AS leaveCnt, "
				+ "SUM(stt.payAmount) AS payAmount, "
				+ "SUM(stt.useAmount) AS useAmount, "
				+ "SUM(stt.salesAmount) AS salesAmount "
			+ "FROM Statistics stt "
			+ "WHERE stt.dateHour BETWEEN :#{#from} AND :#{#to}")
	public Counts findSumByPeroid(@Param("from") String from, @Param("to") String to);
	
	public Integer deleteByDateHour(String dateHour);
}
