package com.been.onlinestore.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.dto.request.UserServiceRequest;

import lombok.Builder;

public record UserRequest() {

	private static final String UID_PATTERN_REGEX = "^[a-z]{1}[a-z0-9]{3,9}$";
	private static final String UID_PATTERN_MESSAGE = "영문 또는 영문 + 숫자 조합 4 ~ 10자리를 입력해주세요.";

	private static final String PHONE_PATTERN_REGEX = "^010([0-9]{7,8})+$";
	private static final String PHONE_PATTERN_MESSAGE = "'-'(하이픈) 없이 10 ~ 11 자리의 숫자만 입력 가능합니다.";

	private static final String EMAIL_PATTERN_REGEX = "^[a-zA-Z0-9]+@[0-9a-zA-Z]+\\.[a-z]{3}+$";
	private static final String EMAIL_PATTERN_MESSAGE = "잘못된 이메일 형식입니다.";

	@Builder
	public record SignUp(
		@NotBlank @Size(max = 50)
		@Pattern(regexp = UID_PATTERN_REGEX, message = UID_PATTERN_MESSAGE)
		String uid,

		@NotBlank @Size(max = 255)
		String password,

		@NotBlank @Size(max = 20)
		String name,

		@NotBlank @Size(max = 100)
		@Pattern(regexp = EMAIL_PATTERN_REGEX, message = EMAIL_PATTERN_MESSAGE)
		String email,

		@Size(max = 20)
		String nickname,

		@NotBlank @Size(max = 20)
		@Pattern(regexp = PHONE_PATTERN_REGEX, message = PHONE_PATTERN_MESSAGE)
		String phone
	) {

		public static SignUp of(String uid, String password, String name, String email, String nickname, String phone) {
			return new SignUp(uid, password, name, email, nickname, phone);
		}

		public UserServiceRequest.SignUp toServiceRequest() {
			return UserServiceRequest.SignUp.of(
				uid,
				password,
				name,
				email,
				nickname,
				phone
			);
		}
	}

	@Builder
	public record Login(
		@NotBlank @Size(max = 50)
		@Pattern(regexp = UID_PATTERN_REGEX, message = UID_PATTERN_MESSAGE)
		String uid,

		@NotBlank @Size(max = 255)
		String password
	) {
	}

	@Builder
	public record Update(
		@Size(max = 20)
		String nickname,

		@NotBlank @Size(max = 20)
		@Pattern(regexp = PHONE_PATTERN_REGEX, message = PHONE_PATTERN_MESSAGE)
		String phone
	) {
	}
}
