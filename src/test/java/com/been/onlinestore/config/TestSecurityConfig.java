package com.been.onlinestore.config;

import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.domain.constant.RoleType;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

	public static final Long SELLER_ID = 1L;
	public static final Long USER_ID = 1L;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.authorizeRequests(auth -> auth.anyRequest().permitAll())
				.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			if (username.equals("seller")) {
				return createPrincipalDetails(SELLER_ID, "seller", RoleType.SELLER);
			}
			if (username.equals("user")) {
				return createPrincipalDetails(USER_ID, "user", RoleType.USER);
			}
			return null;
		};
	}

	private PrincipalDetails createPrincipalDetails(Long id, String uid, RoleType roleType) {
		return PrincipalDetails.of(
				id,
				uid,
				"pw",
				roleType,
				uid,
				uid + "@mail.com",
				uid,
				"01011112222"
		);
	}
}
