package com.daoutech.api.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.daoutech.api.exception.AccessDeniedCustomHandler;
import com.daoutech.api.exception.AuthenticationCustomEntryPoint;
import com.daoutech.api.filter.ApiFilter;
import com.daoutech.api.util.JwtUtil;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
	
	@Value("${app.allow-ip}")
	private String allowIp;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.httpBasic()
					.disable()
				.csrf()
					.disable()
				.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
				.authorizeHttpRequests()
					.antMatchers("/").access((authentication, context) -> new AuthorizationDecision(
							new IpAddressMatcher(allowIp).matches(context.getRequest())))
					.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
					.antMatchers("/api/v1/auth*/**").permitAll()
					.antMatchers("/api/v1/stat/inquiry*/**").hasAnyRole(UserRole.ADMIN.getCode(),UserRole.USER.getCode())
					.antMatchers("/api/v1/stat/regist*/**", "/api/v1/stat/modify*/**", "/api/v1/stat/delete*/**").hasRole(UserRole.ADMIN.getCode())
					.anyRequest().authenticated()
					.and()
				.cors()
					.and()
				.headers()
					.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN))
					.and()
				.exceptionHandling().accessDeniedHandler(new AccessDeniedCustomHandler())
					.and()
				.exceptionHandling().authenticationEntryPoint(new AuthenticationCustomEntryPoint())
					.and()
				.addFilterBefore(new ApiFilter(), UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Bean
	public WebSecurityCustomizer configure() {
		return (web) -> web.ignoring()
				.antMatchers("/favicon.ico", "/h2-console/**");
	}
	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean    
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addAllowedOrigin("*");
		configuration.setAllowedMethods(Arrays.asList(
				HttpMethod.OPTIONS.name(), 
				HttpMethod.HEAD.name(), 
				HttpMethod.GET.name(), 
				HttpMethod.POST.name(), 
				HttpMethod.PUT.name(), 
				HttpMethod.DELETE.name()
				));
		configuration.addExposedHeader(JwtUtil.ACCESS_TOKEN); 
		configuration.addAllowedHeader("*");
		
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
