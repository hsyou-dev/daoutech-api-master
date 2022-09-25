package com.daoutech.api.domain.user.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.daoutech.api.domain.user.dto.UserDto;
import com.daoutech.api.domain.user.entity.User;
import com.daoutech.api.security.UserRole;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {
	}
	
	private static Stream<UserDto> dummyDto() {
		return Stream.of(UserDto.builder()
				.userId("dummyId")
				.userPw("dummyPw")
				.userNm("dummyNm")
				.userType(UserRole.USER.getCode())
				.build());
	}

	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 아이디 조회 - 등록된 아이디가 있는 경우")
	void findById(UserDto dummyDto) {
		// given
		userRepository.save(dummyDto.toEntity());
		
		// when
		Optional<User> o = userRepository.findById(dummyDto.getUserId());
		
		// then
		assertTrue(o.isPresent());
		User resultData = o.get();
		assertEquals(dummyDto.getUserId(), resultData.getUserId());
		assertEquals(dummyDto.getUserNm(), resultData.getUserNm());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 아이디 조회 - 등록된 아이디가 없는 경우")
	void findById_emptyData(UserDto dummyDto) {
		// given
		String userId = dummyDto.getUserId();
		
		// when
		Optional<User> o = userRepository.findById(userId);
		
		// then
		assertFalse(o.isPresent());
	}
	
	@ParameterizedTest
	@MethodSource("dummyDto")
	@DisplayName("사용자 등록")
	void save(UserDto dummyDto) {
		// given
		User user = dummyDto.toEntity();
		
		// when
		User resultData = userRepository.save(user);
		
        // then
		assertSame(user, resultData);
	}
}
