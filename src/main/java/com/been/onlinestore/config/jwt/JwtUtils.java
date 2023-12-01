package com.been.onlinestore.config.jwt;

import com.been.onlinestore.exception.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class JwtUtils {

	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
	}

	public static void setErrorResponse(HttpServletResponse response, JwtErrorCode code) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(code.getHttpStatus().value());

		ApiErrorResponse<Void> errorResponse = ApiErrorResponse.fail(code.getMessage());
		String s = mapper.writeValueAsString(errorResponse);

		response.getWriter().write(s);
	}
}
