package com.been.onlinestore.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

import com.been.onlinestore.common.ApiErrorResponse;
import com.been.onlinestore.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ApiResponseUtils {

	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
	}

	private ApiResponseUtils() {
	}

	public static void setSuccessResponse(HttpServletResponse response, HttpStatus status) throws
		IOException {
		initResponse(response, status);
		response.getWriter().write(mapper.writeValueAsString(ApiResponse.success()));
	}

	public static void setErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws
		IOException {
		initResponse(response, status);
		response.getWriter().write(mapper.writeValueAsString(ApiErrorResponse.fail(message)));
	}

	private static void initResponse(HttpServletResponse response, HttpStatus status) {
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
	}
}
