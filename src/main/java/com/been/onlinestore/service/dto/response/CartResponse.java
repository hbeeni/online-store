package com.been.onlinestore.service.dto.response;

import java.util.List;

import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.constant.SaleStatus;

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
			cartProducts.stream()
				.filter(cp -> cp.saleStatus() == SaleStatus.SALE)
				.mapToInt(CartProductResponse::totalPrice)
				.sum(),
			deliveryFee,
			cartProducts
		);
	}
}
