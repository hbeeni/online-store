package com.been.onlinestore.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.request.CategoryServiceRequest;

import lombok.Builder;

public record CategoryRequest() {

	@Builder
	public record Create(
			@NotBlank @Size(max = 20)
			String name,

			@Size(max = 100)
			String description
	) {

		public CategoryServiceRequest.Create toServiceRequest() {
			return CategoryServiceRequest.Create.of(
					name,
					description
			);
		}
	}

	@Builder
	public record Update(
			@NotBlank @Size(max = 20)
			String name,

			@Size(max = 100)
			String description
	) {

		public CategoryServiceRequest.Update toServiceRequest() {
			return CategoryServiceRequest.Update.of(
					name,
					description
			);
		}
	}
}
