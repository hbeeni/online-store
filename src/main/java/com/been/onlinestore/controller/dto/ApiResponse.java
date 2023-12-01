package com.been.onlinestore.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private final String status;
	private final T data;
	private final PageInfo page;

	private ApiResponse(T data, PageInfo page) {
		this.status = "success";
		this.data = data;
		this.page = page;
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
		return new ApiResponse<>(null, null);
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
		return new ApiResponse<>(data, null);
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
		return new ApiResponse<>(Map.of("id", id), null);
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
				page.getContent(),
				PageInfo.of(page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements())
		);
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
