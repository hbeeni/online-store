package com.been.onlinestore.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.Builder;

public record CartProductRequest() {

	@Builder
	public record Create(
		@NotNull @Positive
		Long productId,

		@NotNull @PositiveOrZero
		Integer productQuantity
	) {
	}

	@Builder
	public record Update(
		@NotNull @PositiveOrZero
		Integer productQuantity
	) {
	}
}
