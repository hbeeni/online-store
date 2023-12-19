package com.been.onlinestore.controller.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.been.onlinestore.service.dto.request.CartProductServiceRequest;

public record CartProductRequest() {

	public record Create(
		@NotNull @Positive
		Long productId,

		@NotNull @PositiveOrZero
		Integer productQuantity
	) {

		public CartProductServiceRequest.Create toServiceRequest() {
			return CartProductServiceRequest.Create.of(
				productId,
				productQuantity
			);
		}
	}

	public record Update(
		@NotNull @PositiveOrZero
		Integer productQuantity
	) {
	}
}
