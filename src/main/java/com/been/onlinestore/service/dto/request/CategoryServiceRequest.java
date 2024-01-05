package com.been.onlinestore.service.dto.request;

import com.been.onlinestore.domain.Category;

public record CategoryServiceRequest() {

	public record Create(
		String name,
		String description
	) {

		public static Create of(String name, String description) {
			return new Create(name, description);
		}

		public Category toEntity() {
			return Category.of(
				name,
				description
			);
		}
	}

	public record Update(
		String name,
		String description
	) {

		public static Update of(String name, String description) {
			return new Update(name, description);
		}
	}
}
