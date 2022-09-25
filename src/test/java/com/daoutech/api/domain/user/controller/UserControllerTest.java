package com.daoutech.api.domain.user.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.daoutech.api.domain.user.dto.UserDto;
import com.daoutech.api.domain.user.service.UserService;
import com.daoutech.api.exception.ServiceError;
import com.daoutech.api.exception.ServiceExceptionHandler;
import com.daoutech.api.security.UserRole;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	
	@Mock
	private UserService userService;
	@InjectMocks
	private UserController userController;
	private MockMvc mockMvc;
	
	@BeforeEach
	void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(userController)
				.setControllerAdvice(new ServiceExceptionHandler())
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.build();
		
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
	 * **************************************SignUp************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 등록 API")
	void signUpSuccess(UserDto dummyDto) throws Exception {
		// given
		when(userService.signUp(any())).thenReturn(dummyDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/signUp")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.userId", equalTo(dummyDto.getUserId())))
				.andExpect(jsonPath("$.data.userNm", equalTo(dummyDto.getUserNm())))
				.andExpect(jsonPath("$.data.userType", equalTo(dummyDto.getUserType())))
				.andDo(print())
				.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 등록 API - 필수 값이 유효하지 않은 경우")
	void signUpFail_emptyParam(UserDto dummyDto) throws Exception {
		// given
		dummyDto.setUserId(null);
		dummyDto.setUserPw(null);
		
		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/signUp")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}
	
	
	/**
	 * **************************************SignIn************************************** 
	 */
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 인증 API")
	void signIn(UserDto dummyDto) throws Exception {
		// given
		when(userService.signIn(any())).thenReturn(dummyDto);
		
		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/signIn")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(HttpStatus.OK.value())))
				.andExpect(jsonPath("$.data.userId", equalTo(dummyDto.getUserId())))
				.andExpect(jsonPath("$.data.userNm", equalTo(dummyDto.getUserNm())))
				.andExpect(header().exists("Access-Token"))
				.andDo(print())
				.andReturn();
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 인증 API - 필수 값이 유효하지 않은 경우")
	void signInFail_emptyParam(UserDto dummyDto) throws Exception {
		// given
		dummyDto.setUserId(null);
		dummyDto.setUserPw(null);
		
		// when
		ResultActions resultActions = mockMvc.perform(post("/api/v1/auth/signIn")
				.content(new Gson().toJson(dummyDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		// then
		resultActions
				.andExpect(status().is4xxClientError())
				.andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
				.andExpect(jsonPath("$.message", containsString(ServiceError.INVALID_REQUEST_PARAM.getDescription())))
				.andDo(print())
				.andReturn();
	}

}
