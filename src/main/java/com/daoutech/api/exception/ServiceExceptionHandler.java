package com.daoutech.api.exception;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.daoutech.api.util.ApiResponse;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {

	private String getErrorMsg(List<String> parameterNames) {
		return new StringBuilder(ServiceError.INVALID_REQUEST_PARAM.getDescription())
    			.append("(")
    			.append(String.join(", ", parameterNames))
    			.append(")")
    			.toString();
	}
    
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleServiceException(ServiceException se) {
    	log.error("[error]status = {}, message = {}", se.getStatus(), se.getMessage());
    	return new ResponseEntity<>(ApiResponse.error(se.getMessage(), se.getStatus()), se.getStatus());
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleRequestParameterException(MissingServletRequestParameterException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(
    			this.getErrorMsg(Arrays.asList(e.getParameterName())), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	
    	List<String> parameterNames = e.getConstraintViolations().stream()
    			.map(error -> error.getMessage())
    			.collect(Collectors.toList());
    	
    	return new ResponseEntity<>(ApiResponse.error(
    			this.getErrorMsg(parameterNames), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	
    	List<String> parameterNames = e.getBindingResult().getAllErrors().stream()
    			.map(error -> error.getDefaultMessage())
    			.collect(Collectors.toList());
    	
    	return new ResponseEntity<>(ApiResponse.error(
    			this.getErrorMsg(parameterNames), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.FORBIDDEN), HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<?> handleHttpMediaTypeException(HttpMediaTypeException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotAllowedException(HttpMediaTypeNotSupportedException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.METHOD_NOT_ALLOWED), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<?> handleException(Exception e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<?> handleRateLimiterRequestNotPermitted(Throwable t) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", t.getClass(), t.getMessage(), t.getCause());
    	return new ResponseEntity<>(ApiResponse.error("요청이 제한되었습니다.", HttpStatus.TOO_MANY_REQUESTS), HttpStatus.TOO_MANY_REQUESTS);
    }
    
    //database exception
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.CONFLICT), HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<?> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException e) {
    	log.error("[error] Exception = {}, message = {}, cause = {}", e.getClass(), e.getMessage(), e.getCause());
    	return new ResponseEntity<>(ApiResponse.error(e, HttpStatus.UNAUTHORIZED), HttpStatus.UNAUTHORIZED);
    }
}
