package com.daoutech.api.exception;

import org.springframework.http.HttpStatus;

public enum ServiceError {
	INVALID_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "Invalid request parameter"),
	NOTFOUND_DATA(HttpStatus.NOT_FOUND, "Not found data"),
	CONFLICT_DATA(HttpStatus.CONFLICT, "Conflict data"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access Denied"),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
	
	private final HttpStatus statusCode;
	
    private final String description;

    private ServiceError(HttpStatus statusCode, String description) {
    	this.statusCode = statusCode;
        this.description = description;
    }

    public HttpStatus getStatusCode() {
		return statusCode;
	}

	public String getDescription() {
        return description;
    }
}
