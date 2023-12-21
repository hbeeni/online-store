package com.been.onlinestore.exception;

import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.been.onlinestore.common.ApiErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionControllerAdvice {

	@ExceptionHandler({EntityNotFoundException.class, IllegalArgumentException.class})
	public ResponseEntity<ApiErrorResponse<Void>> badRequest(Exception ex) {
		return ResponseEntity.badRequest().body(ApiErrorResponse.fail(ex));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiErrorResponse<Void>> failAuthentication(Exception ex) {
		Throwable throwable = ex.getCause() == null ? ex : ex.getCause();
		log.error(throwable.getMessage(), throwable);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiErrorResponse.fail("로그인에 실패하였습니다."));
	}

	/**
	 * &#064;RequestParam으로  설정한 파라미터 또는 &#064;RequestPart가 입력되지 않았을 경우 발생하는 예외를 처리한다.
	 */
	@ExceptionHandler({MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
	public ResponseEntity<ApiErrorResponse<Void>> missingParameter(Exception ex) {
		return ResponseEntity.badRequest().body(ApiErrorResponse.fail(ex));
	}

	/**
	 * &#064;RequestBody로 설정한 객체를 만들지 못할 때 발생하는 예외를 처리한다.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse<Void>> notValidRequestData(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest().body(ApiErrorResponse.fail(ex));
	}

	/**
	 * &#064;RequestBody로 설정한 객체의 유효성 검증이 실패할 때 발생하는 예외를 처리한다.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse<Map<String, String>>> validationFail(MethodArgumentNotValidException ex) {
		return ResponseEntity.badRequest().body(ApiErrorResponse.fail(ex.getBindingResult()));
	}

	/**
	 * &#064;ModelAttribute로 설정한 객체의 유효성 검증이 실패할 때 발생하는 예외를 처리한다.
	 */
	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiErrorResponse<Map<String, String>>> bindingFail(BindException ex) {
		return ResponseEntity.badRequest().body(ApiErrorResponse.fail(ex.getBindingResult()));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiErrorResponse<Void>> runtimeEx(RuntimeException ex) {
		Throwable throwable = ex.getCause() == null ? ex : ex.getCause();
		log.error(throwable.getMessage(), throwable);
		return ResponseEntity.badRequest().body(ApiErrorResponse.fail(ex));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse<Void>> internalServerError(Exception ex) {
		Throwable throwable = ex.getCause() == null ? ex : ex.getCause();
		log.error(throwable.getMessage(), throwable);
		return ResponseEntity.internalServerError().body(ApiErrorResponse.error(ex));
	}
}
