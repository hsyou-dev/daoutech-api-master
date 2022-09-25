package com.daoutech.api.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.daoutech.api.domain.user.dto.UserDto;
import com.daoutech.api.security.UserRole;

public class JwtUtilTest {

	@BeforeEach
	void setUp() throws Exception {
	}
	
	private static Stream<UserDto> dummyDto() {
		return Stream.of(UserDto.builder()
				.userId("dummyId")
				.userPw("dummyPw")
				.userNm("dummyNm")
				.userType(UserRole.USER.getCode())
				.build());
	}
	
	private static Stream<String> dummyToken() {
		return Stream.of(JwtUtil.create(UserDto.builder()
				.userId("dummyId")
				.userNm("dummyNm")
				.userType(UserRole.USER.getCode())
				.build()));
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("토큰 발행")
	public void createToken(UserDto dummyDto) throws Exception {
		String token = JwtUtil.create(UserDto.builder()
				.userId(dummyDto.getUserId())
				.userNm(dummyDto.getUserNm())
				.userType(dummyDto.getUserType())
				.build());
		
		assertNotNull(token);
	}
	
	@ParameterizedTest
	@MethodSource("dummyToken")
	@DisplayName("토큰 유효여부 검사")
	public void isValid(String token) throws Exception {
		boolean valid = JwtUtil.isValid(token);
		
		assertTrue(valid);
	}
}
