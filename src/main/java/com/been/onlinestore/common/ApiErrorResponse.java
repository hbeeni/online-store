package com.been.onlinestore.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse<T> {

	private static final String STATUS_FAIL = "fail";
	private static final String STATUS_ERROR = "error";

	private final String status;
	private final T data;
	private final String message;

	private ApiErrorResponse(String status, T data, String message) {
		this.status = status;
		this.data = data;
		this.message = message;
	}

	/**
	 * <p>실패 응답(필드 에러) - 실패 응답 상태 코드, 필드 에러 데이터, 메시지를 반환합니다.</p>
	 * <pre>
	 * {
	 *     "status": "fail",
	 *     "data": "필드 에러 데이터"
	 *     "message": "실패 메시지"
	 * }
	 * </pre>
	 */
	public static ApiErrorResponse<Map<String, String>> fail(BindingResult bindingResult) {
		Map<String, String> errorMap = new HashMap<>();
		bindingResult.getFieldErrors()
			.forEach(b -> errorMap.put(b.getField(), b.getDefaultMessage()));
		return new ApiErrorResponse<>(STATUS_FAIL, errorMap, "field errors");
	}

	/**
	 * <p>에러 응답 - 실패 응답 상태 코드, 메시지를 반환합니다.</p>
	 * <pre>
	 * {
	 *     "status": "fail",
	 *     "message": "실패 메시지"
	 * }
	 * </pre>
	 */
	public static ApiErrorResponse<Void> fail(Exception ex) {
		return new ApiErrorResponse<>(STATUS_FAIL, null, ex.getLocalizedMessage());
	}

	/**
	 * <p>에러 응답 - 실패 응답 상태 코드, 메시지를 반환합니다.</p>
	 * <pre>
	 * {
	 *     "status": "fail",
	 *     "message": "실패 메시지"
	 * }
	 * </pre>
	 */
	public static ApiErrorResponse<Void> fail(String message) {
		return new ApiErrorResponse<>(STATUS_FAIL, null, message);
	}

	/**
	 * <p>에러 응답 - 에러 응답 상태 코드, 메시지를 반환합니다.</p>
	 * <pre>
	 * {
	 *     "status": "error",
	 *     "message": "에러 메시지"
	 * }
	 * </pre>
	 */
	public static ApiErrorResponse<Void> error(Exception ex) {
		return new ApiErrorResponse<>(STATUS_ERROR, null, ex.getLocalizedMessage());
	}
}
