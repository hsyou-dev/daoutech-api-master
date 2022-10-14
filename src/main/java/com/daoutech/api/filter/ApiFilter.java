package com.daoutech.api.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

import com.daoutech.api.util.ApiResponse;
import com.daoutech.api.util.ApiResponse.ApiResult;
import com.daoutech.api.util.JwtUtil;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiFilter extends GenericFilter {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException, ServiceException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		
		log.info("request uri: " + httpServletRequest.getRequestURI());
		
		if(HttpMethod.OPTIONS.name().equals(httpServletRequest.getMethod())) {
			httpServletResponse.setStatus(HttpServletResponse.SC_OK);
			
		} else if(this.beforeAuth(httpServletRequest.getRequestURI())) {
			chain.doFilter(httpServletRequest, httpServletResponse);
		} else {
			String token = httpServletRequest.getHeader(JwtUtil.ACCESS_TOKEN);
            if(!ObjectUtils.isEmpty(token) && JwtUtil.isValid(token)) {
        		Authentication auth = JwtUtil.getAuthenticationBy(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                
                chain.doFilter(httpServletRequest, httpServletResponse);
            } else {
            	httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            	httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                
        		PrintWriter writer = httpServletResponse.getWriter();
        		ApiResult<?> apiResult = ApiResponse.error(
        				"토큰이 유효하지 않습니다. 로그인 후 다시 시도해주세요.", HttpStatus.UNAUTHORIZED);
        		String body = new Gson().toJson(apiResult);
        		writer.print(body);
            }
		}
	}
	
	/**
	 * 로그인 전 접근 url 여부 확인
	 */
	private boolean beforeAuth(String uri) {
		if(uri.startsWith("/api/v1/auth")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 쿠키 또는 헤더에서 토큰 추출
	 */
	@Deprecated
	private String getToken(HttpServletRequest httpServletRequest) {
		return Optional.ofNullable(httpServletRequest.getCookies())
					.flatMap(arg0 -> Arrays.stream(arg0)
							.filter(cookie -> JwtUtil.ACCESS_TOKEN.equals(cookie.getName()))
							.findFirst())
					.map(Cookie::getValue)
					.orElse(httpServletRequest.getHeader(JwtUtil.ACCESS_TOKEN));
	}
}
