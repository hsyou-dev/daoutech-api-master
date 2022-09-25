package com.daoutech.api.util;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import com.daoutech.api.domain.user.dto.UserDto;
import com.daoutech.api.domain.user.entity.User;
import com.daoutech.api.security.SecurityUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {
	public static final String ACCESS_TOKEN = "Access-Token";
	
	private static final String EMAIL = "erinbbotto@gmail.com";			//secretkey 생성에 사용 
	private static final String ISS = "hsyou";							//토큰 발급자
	private static final String SUB = "daoutech-api-master";			//토큰 제목
	private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 2;	//토근 유효시간(2시간)
	private static final Key SECRET_KEY = new SecretKeySpec(EMAIL.getBytes(), SignatureAlgorithm.HS256.getJcaName());	//sign 키

	/*
	 * 토큰 생성
	 */
	public static String create(UserDto userDto) {
		Date today = new Date(System.currentTimeMillis());
		
		JwtBuilder jwtBuilder = 
				Jwts.builder()
					.setIssuer(ISS)
					.setSubject(SUB)
					.setAudience(userDto.getUserNm())
					.setIssuedAt(today)
					.setExpiration(new Date(today.getTime() + TOKEN_EXPIRE_TIME))
					.claim("userId", userDto.getUserId())
					.claim("userType", userDto.getUserType())
					.signWith(SignatureAlgorithm.HS256, SECRET_KEY);
		
		return jwtBuilder.compact();
	}
	
	/*
	 * 토큰정보로부터 데이터를 추출
	 * security에 설정하기 위한 인증정보 생성 
	 */
	public static Authentication getAuthenticationBy(String token) {
        Claims claims = 
        		Jwts.parser()
        			.setSigningKey(SECRET_KEY)
        			.parseClaimsJws(token)
        			.getBody();

        SecurityUser su = new SecurityUser(User.builder()
        		.userId(claims.get("userId", String.class))
        		.userNm(claims.getAudience())
        		.userType(claims.get("userType", String.class))
        		.build());
        
        return new UsernamePasswordAuthenticationToken(su, null, AuthorityUtils.createAuthorityList(
				"ROLE_" + su.getUser().getUserType()));
    }
	
	/*
	 * 토근 유효성 체크
	 */
	public static boolean isValid(String token) {
        try {
        	Claims claims = 
        			Jwts.parser()
        				.setSigningKey(SECRET_KEY)
        				.parseClaimsJws(token)
        				.getBody();
        	
        	return claims.getExpiration().after(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
        	log.error("jwtToken parse error!");
            return false;
        }
    }
}
