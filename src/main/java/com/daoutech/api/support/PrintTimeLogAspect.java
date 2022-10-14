package com.daoutech.api.support;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import com.daoutech.api.util.DateTimeUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class PrintTimeLogAspect {

	@Around("@annotation(PrintTimeLog)")
	public Object printTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
		String baseUrl = this.getBaseUrl(joinPoint);
		String methodName = ((CodeSignature)joinPoint.getSignature()).getName();
		
		long start = System.currentTimeMillis();
		this.printLog("시작시간", baseUrl, methodName, DateTimeUtil.toDateTimeMillsStringFrom(new Date(start)));
		
		Object proceed = joinPoint.proceed();
		
		long end = System.currentTimeMillis();
		this.printLog("종료시간", baseUrl, methodName, DateTimeUtil.toDateTimeMillsStringFrom(new Date(end)));
		this.printLog("소요시간", baseUrl, methodName, (end - start) + " ms");
		
		return proceed;
	}
	
	private String getBaseUrl(JoinPoint joinPoint) {
		 Class<? extends Object> clas = joinPoint.getTarget().getClass();
		 RequestMapping requestMapping = (RequestMapping) clas.getAnnotation(RequestMapping.class);
		 return requestMapping.value()[0];
	}
	
	private void printLog(String title, String arg1, String arg2, String arg3) {
		log.info("요청 URL: {}, method: {}, " + title + ": {}", arg1, arg2, arg3);
	}
}
