package com.been.onlinestore.service.response.admin;

import static com.been.onlinestore.service.response.JsonFormatConst.*;

import java.time.LocalDateTime;

import com.been.onlinestore.domain.Category;
import com.fasterxml.jackson.annotation.JsonFormat;

public record AdminCategoryResponse(
		Long id,
		String name,
		String description,
		int productCount,
		@JsonFormat(pattern = DATE_TIME_PATTERN) LocalDateTime createdAt,
		String createdBy,
		@JsonFormat(pattern = DATE_TIME_PATTERN) LocalDateTime modifiedAt,
		String modifiedBy
) {

	public static AdminCategoryResponse of(Long id, String name, String description, int productCount,
			LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
		return new AdminCategoryResponse(id, name, description, productCount, createdAt, createdBy, modifiedAt,
				modifiedBy);
	}

	public static AdminCategoryResponse from(Category entity) {
		return AdminCategoryResponse.of(
				entity.getId(),
				entity.getName(),
				entity.getDescription(),
				entity.getProducts().size(),
				entity.getCreatedAt(),
				entity.getCreatedBy(),
				entity.getModifiedAt(),
				entity.getModifiedBy()
		);
	}
}
