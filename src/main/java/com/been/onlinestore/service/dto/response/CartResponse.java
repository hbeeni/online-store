package com.been.onlinestore.service.dto.response;

import java.util.List;

import com.been.onlinestore.domain.CartProduct;

public record CartResponse(
	int totalPrice,
	int deliveryFee,
	List<CartProductResponse> cartProducts
) {

	public static CartResponse of(int totalPrice, int deliveryFee, List<CartProductResponse> cartProducts) {
		return new CartResponse(totalPrice, deliveryFee, cartProducts);
	}

	public static CartResponse from(List<CartProduct> entities, int deliveryFee) {
		List<CartProductResponse> cartProducts = entities.stream()
			.map(CartProductResponse::from)
			.toList();

		return CartResponse.of(
			cartProducts.stream().mapToInt(CartProductResponse::totalPrice).sum(),
			deliveryFee,
			cartProducts
		);
	}
}
