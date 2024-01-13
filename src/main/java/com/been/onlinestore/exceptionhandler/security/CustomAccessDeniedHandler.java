package com.been.onlinestore.exceptionhandler.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.been.onlinestore.util.ApiResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(
		HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException
	) throws IOException {
		log.info("해당 권한으로는 접근할 수 없습니다. uri = {}", request.getRequestURI());
		ApiResponseUtils.setErrorResponse(response, HttpStatus.FORBIDDEN, "해당 권한으로는 접근할 수 없습니다.");
	}
}
