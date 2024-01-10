package com.been.onlinestore.controller.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.dto.request.CartProductServiceRequest;

public record CartProductRequest() {

	private static final String QUANTITY_MESSAGE = "한 개 이상의 상품을 담아주세요.";

	public record Create(
		@NotNull @Positive
		Long productId,

		@NotNull @Positive(message = QUANTITY_MESSAGE)
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

		@NotEmpty @Size(max = 50)
		String deliveryAddress,

		@NotEmpty @Size(max = 20)
		String receiverName,

		@NotEmpty @Size(max = 20)
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
		@NotNull @Positive(message = QUANTITY_MESSAGE)
		Integer productQuantity
	) {
	}
}
