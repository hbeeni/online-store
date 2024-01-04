package com.been.onlinestore.controller.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

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

	public record Order(
		@NotNull
		List<@Positive Long> cartProductIds,

		@NotEmpty
		@Size(max = 50)
		String deliveryAddress,

		@NotEmpty
		@Size(max = 20)
		String receiverName,

		@NotEmpty
		@Size(max = 20)
		@Pattern(regexp = "^010([0-9]{7,8})+$", message = "'-'(하이픈) 없이 10 ~ 11 자리의 숫자만 입력 가능합니다.")
		String receiverPhone
	) {

		public CartProductServiceRequest.Order toServiceRequest() {
			return CartProductServiceRequest.Order.of(
				cartProductIds,
				deliveryAddress,
				receiverName,
				receiverPhone
			);
		}
	}

	public record Update(
		@NotNull @PositiveOrZero
		Integer productQuantity
	) {
	}
}
