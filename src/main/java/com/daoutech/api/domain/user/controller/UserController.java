package com.daoutech.api.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.daoutech.api.domain.user.dto.UserDto;
import com.daoutech.api.domain.user.service.UserService;
import com.daoutech.api.domain.user.support.UserValidGroups.commonValidGroup;
import com.daoutech.api.domain.user.support.UserValidGroups.registValidGroup;
import com.daoutech.api.support.PrintTimeLog;
import com.daoutech.api.util.ApiResponse;
import com.daoutech.api.util.ApiResponse.ApiResult;
import com.daoutech.api.util.JwtUtil;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;

@RateLimiter(name = "userApiLimiter")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
	
	private final UserService userService;
	
	@PrintTimeLog
	@PostMapping("/signUp")
	public ResponseEntity<ApiResult<UserDto>> signUp(@RequestBody @Validated(
			value = {commonValidGroup.class, registValidGroup.class}) UserDto requestDto) {
		return new ResponseEntity<>(ApiResponse.success(userService.signUp(requestDto)), HttpStatus.OK);
	}
	
	@PrintTimeLog
	@PostMapping("/signIn")
	public ResponseEntity<ApiResult<UserDto>> signIn(@RequestBody @Validated(
			commonValidGroup.class) UserDto requestDto) {
		UserDto result = userService.signIn(requestDto);
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set(JwtUtil.ACCESS_TOKEN, JwtUtil.create(result));
		return new ResponseEntity<>(ApiResponse.success(result), headers, HttpStatus.OK);
	}
	
}
