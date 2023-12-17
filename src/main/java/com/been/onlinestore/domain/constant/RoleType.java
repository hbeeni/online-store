package com.been.onlinestore.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RoleType {
	USER("ROLE_USER"),
	ADMIN("ROLE_ADMIN");

	@Getter
	private final String roleName;
}
