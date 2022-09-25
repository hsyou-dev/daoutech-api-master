package com.daoutech.api.domain.user.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daoutech.api.domain.user.dto.UserDto;
import com.daoutech.api.domain.user.entity.User;
import com.daoutech.api.domain.user.repository.UserRepository;
import com.daoutech.api.exception.ServiceError;
import com.daoutech.api.exception.ServiceException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	@Transactional
	public UserDto signUp(UserDto requestDto) throws ServiceException {
		userRepository.findById(requestDto.getUserId())
			.ifPresent(o -> {
				throw new ServiceException(ServiceError.CONFLICT_DATA, "이미 등록된 아이디입니다: " + o.getUserId());
			});
		
		requestDto.setUserPw(passwordEncoder.encode(requestDto.getUserPw()));
		return UserDto.from(userRepository.save(requestDto.toEntity()));
	}
	
	@Transactional
	public UserDto signIn(UserDto requestDto) throws ServiceException {
		User user = userRepository.findById(requestDto.getUserId())
			.orElseThrow(() -> new ServiceException(ServiceError.UNAUTHORIZED, "아이디를 찾을 수 없습니다: "));
		
		if (!passwordEncoder.matches(requestDto.getUserPw(), user.getUserPw())) {
			throw new ServiceException(ServiceError.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");
		}

		user.setFnlLoginDttm(Timestamp.valueOf(LocalDateTime.now()));
		return UserDto.from(user);
	}
	
}
