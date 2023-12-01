package com.been.onlinestore.config.jwt;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.been.onlinestore.config.jwt.JwtUtils.setErrorResponse;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.info("해당 권한으로는 접근할 수 없습니다. uri = {}", request.getRequestURI());
		setErrorResponse(response, JwtErrorCode.FORBIDDEN_REQUEST);
	}
}
