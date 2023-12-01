package com.been.onlinestore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionControllerAdvice {

    @ExceptionHandler({EntityNotFoundException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse<Void>> badRequest(Exception e) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.fail(e));
    }

    /**
     * &#064;RequestParam으로  설정한 파라미터 또는 &#064;RequestPart가 입력되지 않았을 경우 발생하는 예외를 처리한다.
     */
    @ExceptionHandler({MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApiErrorResponse<Void>> missingParameter(Exception e) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.fail(e));
    }

    /**
     * &#064;RequestBody로 설정한 객체를 만들지 못할 때 발생하는 예외를 처리한다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse<Void>> notValidRequestData(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.fail(e));
    }

    /**
     * &#064;RequestBody로 설정한 객체의 유효성 검증이 실패할 때 발생하는 예외를 처리한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse<Map<String, String>>> validationFail(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.fail(e.getBindingResult()));
    }

    /**
     * &#064;ModelAttribute로 설정한 객체의 유효성 검증이 실패할 때 발생하는 예외를 처리한다.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiErrorResponse<Map<String, String>>> bindingFail(BindException e) {
        return ResponseEntity.badRequest().body(ApiErrorResponse.fail(e.getBindingResult()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse<Void>> runtimeEx(RuntimeException e) {
        Throwable t = e.getCause() == null ? e : e.getCause();
        log.error(t.getMessage());
        return ResponseEntity.badRequest().body(ApiErrorResponse.fail(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse<Void>> internalServerError(Exception e) {
        Throwable t = e.getCause() == null ? e : e.getCause();
        log.error(t.getMessage());
        return ResponseEntity.internalServerError().body(ApiErrorResponse.error(e));
    }
}
