package com.daoutech.api.util;

import org.springframework.http.HttpStatus;

public class ApiResponse {
	
	private static final String OK = "Success";

	public static <T> ApiResult<T> success(T data) {
		return new ApiResult<>(HttpStatus.OK.value(), OK, data);
	}
	
	public static <T> ApiResult<T> success(String message, T data) {
		return new ApiResult<>(HttpStatus.OK.value(), message, data);
	}

	public static ApiResult<?> error(Throwable throwable, HttpStatus status) {
		return new ApiResult<>(status.value(), throwable.getMessage(), null);
	}

	public static ApiResult<?> error(String message, HttpStatus status) {
		return new ApiResult<>(status.value(), message, null);
	}
	
	public static ApiResult<?> error(String message, int statusCode) {
		return new ApiResult<>(statusCode, message, null);
	}

	public static class ApiResult<T> {
		private final int status;
		private final String message;
		private final T data;

	    private ApiResult(int status, String message, T data) {
	    	this.status = status;
	    	this.message = message;
	    	this.data = data;
	    }
	
	    public int getStatus() {
			return status;
		}
	
		public String getMessage() {
			return message;
		}
	
		public T getData() {
			return data;
		}
	}
}