package com.been.onlinestore.exceptionhandler.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.been.onlinestore.util.ApiResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(
		HttpServletRequest request, HttpServletResponse response, AuthenticationException authException
	) throws IOException {
		log.info("로그인이 필요합니다. uri = {}", request.getRequestURI());
		ApiResponseUtils.setErrorResponse(response, HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
	}
}
