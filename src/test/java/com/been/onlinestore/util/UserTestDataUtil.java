package com.been.onlinestore.util;

import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;

public class UserTestDataUtil {

	public static User createUser() {
		return createUser("testuser");
	}

	public static User createUser(String uid) {
		User user = User.of(
			uid,
			"testpassword",
			"test user",
			"e@mail.com",
			"test user",
			"01012341234",
			RoleType.USER
		);
		ReflectionTestUtils.setField(user, "id", 1L);
		return user;
	}

	public static User createUser(Long id) {
		User user = User.of(
			"uid",
			"testpassword",
			"test user",
			"e@mail.com",
			"test user",
			"01012341234",
			RoleType.USER
		);
		ReflectionTestUtils.setField(user, "id", id);
		return user;
	}

	public static User createAdmin() {
		return createAdmin("testadmin");
	}

	public static User createAdmin(String uid) {
		User admin = User.of(
			uid,
			"testpassword",
			"test admin",
			"e@mail.com",
			"test admin",
			"01012341234",
			RoleType.ADMIN
		);
		ReflectionTestUtils.setField(admin, "id", 1L);
		return admin;
	}
}
