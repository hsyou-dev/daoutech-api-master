package com.daoutech.api.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 583546929022336121L;

	private HttpStatus status;
	private String message;

	public ServiceException(ServiceError serviceError) {
		super();
		this.status = serviceError.getStatusCode();
		this.message = serviceError.getDescription();
	}

	public ServiceException(ServiceError serviceError, Throwable throwable) {
		super(throwable);
		this.status = serviceError.getStatusCode();
		this.message = serviceError.getDescription() + "(" + throwable.getMessage() + ")";
	}

	public ServiceException(ServiceError serviceError, String message) {
		super(message);
		this.status = serviceError.getStatusCode();
		this.message = serviceError.getDescription() + "(" + message + ")";
	}

}