package com.been.onlinestore.config;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.been.onlinestore.security.jwt.filter.JwtExceptionHandlerFilter;
import com.been.onlinestore.security.jwt.filter.JwtFilter;
import com.been.onlinestore.security.jwt.util.JwtProperties;
import com.been.onlinestore.security.jwt.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

	private final JwtProperties properties;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void configure(HttpSecurity http) {
		http
			.addFilterBefore(
				new JwtFilter(properties, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class
			)
			.addFilterBefore(new JwtExceptionHandlerFilter(), JwtFilter.class);
	}
}
