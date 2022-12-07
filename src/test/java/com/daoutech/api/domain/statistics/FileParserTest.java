package com.daoutech.api.domain.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.daoutech.api.domain.statistics.dto.StatisticsDto;
import com.daoutech.api.domain.statistics.support.FileParser;

public class FileParserTest {
	public static final String DIR_PATH = "./sample";
	public static final String FILE_PATH = "./sample/test.dat";
	
	@BeforeEach
    void setUp() throws IOException {
		File df = new File(DIR_PATH);
		if(!df.isDirectory()) {
			df.mkdirs();
		}
		
		File f = new File(FILE_PATH);
		if(f.exists()) {
			f.delete();
		}
    }
	
	//브랜치 테스트
	//2022-07-22 01|12|9|22,100|5,300|125,000
	//2022-07-22 01,12,9,"22,100","5,300","125,000"
	//2022-07-22 00	30	4	"45,100"	"27,300"	"95,000"
	@Test
	@DisplayName("통계정보 파일쓰기 읽기 테스트")
	public void writeNRead() {
		int dummyCnt = 24;
		for(int i=0; i<dummyCnt; i++) {
			FileParser.writeDataTo(FILE_PATH);
		}
		List<StatisticsDto> list = FileParser.readDataFrom(FILE_PATH);
		
		assertNotNull(list);
		assertEquals(dummyCnt, list.size());
	}
}
