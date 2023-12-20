package com.been.onlinestore.security.jwt.exception;

import static com.been.onlinestore.security.jwt.util.JwtUtils.*;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException {
		log.info("해당 권한으로는 접근할 수 없습니다. uri = {}", request.getRequestURI());
		setErrorResponse(response, JwtErrorCode.FORBIDDEN_REQUEST);
	}
}
