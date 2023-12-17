package com.been.onlinestore.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.domain.constant.RoleType;

@TestConfiguration
public class TestSecurityConfig {

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
			if (username.equals("user")) {
				return PrincipalDetails.of(
					USER_ID,
					"user",
					"pw",
					RoleType.USER,
					"user",
					"user@mail.com",
					"user",
					"01011112222"
				);
			}
			return null;
		};
	}
}
