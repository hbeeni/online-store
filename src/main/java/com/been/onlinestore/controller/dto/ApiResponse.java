package com.been.onlinestore.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private static final String STATUS_SUCCESS = "success";
    private static final String STATUS_FAIL = "fail";
    private static final String STATUS_ERROR = "error";

    private String status;
    private T data;
    private PageInfo page;
    private String message;

    private ApiResponse(String status, T data, PageInfo page, String message) {
        this.status = status;
        this.data = data;
        this.page = page;
        this.message = message;
    }

    /**
     * <p>성공 응답 - 성공 응답 상태 코드를 반환합니다.</p>
     * <pre>
     * {
     *     "status": "success",
     * }
     * </pre>
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(STATUS_SUCCESS, null, null, null);
    }

    /**
     * <p>성공 응답 - 성공 응답 상태 코드, 데이터를 반환합니다.</p>
     * <pre>
     * {
     *     "status": "success",
     *     "data": "단일 데이터 또는 다중 데이터(배열)"
     * }
     * </pre>
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(STATUS_SUCCESS, data, null, null);
    }

    /**
     * <p>성공 응답 - 성공 응답 상태 코드, 데이터(id)를 반환합니다.</p>
     * <pre>
     * {
     *     "status": "success",
     *     "data": {
     *         "id": 1
     *     }
     * }
     * </pre>
     */
    public static ApiResponse<Map<String, Long>> successId(Long id) {
        return new ApiResponse<>(STATUS_SUCCESS, Map.of("id", id), null, null);
    }

    /**
     * <p>성공 응답 - 성공 응답 상태 코드, 데이터, 페이지네이션 정보를 반환합니다.</p>
     * <pre>
     * {
     *     "status": "success",
     *     "data": "다중 데이터(배열)"
     *     "page": {
     *         "number": 1
     *         "size": 10
     *         "totalPages": 10
     *         "totalElements": 100
     *     }
     * }
     * </pre>
     */
    public static <T> ApiResponse<List<T>> pagination(Page<T> page) {
        return new ApiResponse<>(
                STATUS_SUCCESS,
                page.getContent(),
                PageInfo.of(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements()),
                null
        );
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
    public static ApiResponse<Map<String, String>> fail(BindingResult bindingResult) {
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors()
                .forEach(b -> errorMap.put(b.getField(), b.getDefaultMessage()));
        return new ApiResponse<>(STATUS_FAIL, errorMap, null, "field errors");
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
    public static ApiResponse<?> fail(String message) {
        return new ApiResponse<>(STATUS_FAIL, null, null, message);
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
    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>(STATUS_ERROR, null, null, message);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    static class PageInfo {

        private int number;
        private int size;
        private int totalPages;
        private long totalElements;

        /**
         * @param number        현재 페이지
         * @param size          한 페이지당 사이즈
         * @param totalPages    전체 페이지 수
         * @param totalElements 전체 데이터 수
         */
        public static PageInfo of(int number, int size, int totalPages, long totalElements) {
            return new PageInfo(number, size, totalPages, totalElements);
        }
    }
}
