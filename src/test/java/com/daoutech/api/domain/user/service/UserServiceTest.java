package com.daoutech.api.domain.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.daoutech.api.domain.user.dto.UserDto;
import com.daoutech.api.domain.user.repository.UserRepository;
import com.daoutech.api.exception.ServiceException;
import com.daoutech.api.security.UserRole;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	
	@Mock
	private UserRepository userRepository;
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	@InjectMocks
	private UserService userService;

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
	
	
	/**
	 * SignUp 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 등록 성공 - 동일 ID의 사용자가 등록되어 있지 않은 경우")
	void signUpSuccess(UserDto dummyDto) {
		// given
		when(userRepository.findById(any())).thenReturn(Optional.empty());
		when(userRepository.save(any())).thenReturn(dummyDto.toEntity());
		
		// when
		UserDto resultDto = userService.signUp(dummyDto);
		
		// then
		assertNotNull(resultDto);
		assertEquals(dummyDto.getUserId(), resultDto.getUserId());
		assertEquals(dummyDto.getUserNm(), resultDto.getUserNm());
	}

	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 등록 실패 - 동일 ID의 사용자가 등록되어 있는 경우")
	void signUpFail_conflictId(UserDto dummyDto) {
		// given
		when(userRepository.findById(any()))
				.thenReturn(Optional.of(dummyDto.toEntity()));
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			userService.signUp(dummyDto);
	    });
		
		// then
		assertEquals(true, exception instanceof ServiceException);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.CONFLICT, se.getStatus());
	}
	
	
	/**
	 * SignIn
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 인증 성공 - 등록된 사용자가 있고, 비밀번호가 일치할 경우")
	void signInSuccess(UserDto dummyDto) {
		// given
		when(userRepository.findById(any())).thenReturn(Optional.of(dummyDto.toEntity()));
		when(passwordEncoder.matches(any(), any())).thenReturn(true);
		
		// when
		UserDto resultDto = userService.signIn(dummyDto);
		
		// then
		assertNotNull(resultDto);
		assertEquals(dummyDto.getUserId(), resultDto.getUserId());
		assertEquals(dummyDto.getUserNm(), resultDto.getUserNm());
		assertNotNull(resultDto.getFnlLoginDttm());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 인증 실패 - 등록된 사용자가 있고, 비밀번호가 일치하지 않을 경우")
	void signInFail_incorrectPwd(UserDto dummyDto) {
		// given
		when(userRepository.findById(any())).thenReturn(Optional.of(dummyDto.toEntity()));
		when(passwordEncoder.matches(any(), any())).thenReturn(false);
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			userService.signIn(dummyDto);
	    });
		
		// then
		assertEquals(exception instanceof ServiceException, true);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.UNAUTHORIZED, se.getStatus());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 인증 실패 - 등록된 사용자가 없는 경우")
	void signInFail_empty(UserDto dummyDto) {
		// given
		when(userRepository.findById(any())).thenReturn(Optional.empty());
		
		// when
		Throwable exception = assertThrows(RuntimeException.class, () -> {
			userService.signIn(dummyDto);
	    });
		
		// then
		assertEquals(exception instanceof ServiceException, true);
		ServiceException se = (ServiceException) exception;
		assertEquals(HttpStatus.UNAUTHORIZED, se.getStatus());
	}

}
