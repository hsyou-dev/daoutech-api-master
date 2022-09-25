package com.daoutech.api.security;

public enum UserRole {
	USER("U", "user"),
	ADMIN("A", "admin"),
	SYSTEM("S", "system");
	
	String code;
	String defaultValue;
	
	private UserRole(String code, String defaultValue) {
		this.code = code;
		this.defaultValue = defaultValue;
	}

	public String getCode() {
		return code;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	
}
