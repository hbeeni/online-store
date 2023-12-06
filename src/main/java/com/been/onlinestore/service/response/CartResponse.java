package com.been.onlinestore.service.response;

import java.util.Map;

import com.been.onlinestore.domain.Product;

public record CartResponse(
	Long productId,
	String productName,
	int productPrice,
	int quantity,
	int totalPrice,
	int deliveryFee
) {

	public static CartResponse of(Long productId, String productName, int productPrice, int quantity, int totalPrice,
		int deliveryFee) {
		return new CartResponse(productId, productName, productPrice, quantity, totalPrice, deliveryFee);
	}

	public static CartResponse from(Product entity, Map<Long, Integer> productToQuantityMap) {
		int price = entity.getPrice();
		Integer quantity = productToQuantityMap.get(entity.getId());

		return CartResponse.of(
			entity.getId(),
			entity.getName(),
			price,
			quantity,
			price * quantity,
			entity.getDeliveryFee()
		);
	}
}
