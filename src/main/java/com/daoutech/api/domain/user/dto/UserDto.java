package com.daoutech.api.domain.user.dto;

import javax.validation.constraints.NotEmpty;

import com.daoutech.api.domain.user.entity.User;
import com.daoutech.api.domain.user.support.UserValidGroups.commonValidGroup;
import com.daoutech.api.domain.user.support.UserValidGroups.registValidGroup;
import com.daoutech.api.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
	
	@NotEmpty(groups = commonValidGroup.class, message = "userId")
	private String userId;
	@NotEmpty(groups = commonValidGroup.class, message = "userPw")
	private String userPw;
	@NotEmpty(groups = registValidGroup.class,  message = "userNm")
	private String userNm;
	@NotEmpty(groups = registValidGroup.class,  message = "userType")
	//TODO: U, A 유효성처리
	private String userType;
	
	private String fnlLoginDttm;
	
	public static UserDto from(User user) {
		return UserDto.builder()
				.userId(user.getUserId())
				.userNm(user.getUserNm())
				.userType(user.getUserType())
				.fnlLoginDttm(user.getFnlLoginDttm() != null ? DateTimeUtil.toDateTimeStringFrom(user.getFnlLoginDttm()) : null)
				.build();
	}
	
	public User toEntity() {
		return User.builder()
				.userId(userId)
				.userPw(userPw)
				.userNm(userNm)
				.userType(userType)
				.build();
	}
}
