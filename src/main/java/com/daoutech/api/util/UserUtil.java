package com.daoutech.api.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.daoutech.api.domain.user.entity.User;
import com.daoutech.api.security.SecurityUser;

public class UserUtil {

	public static Optional<User> getCurrentLoginUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null) {
			Object principal = authentication.getPrincipal();
			if(principal != null && principal instanceof SecurityUser) {
				SecurityUser su = (SecurityUser) principal;
				return Optional.of(su.getUser());
			}
		}
		return Optional.empty();
	}
}
