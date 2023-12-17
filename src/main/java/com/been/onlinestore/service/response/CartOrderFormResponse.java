package com.been.onlinestore.service.response;

import java.util.List;

import com.been.onlinestore.domain.Product;

public record CartOrderFormResponse(
	int totalPrice,
	List<CartOrderProductResponse> products
) {

	public static CartOrderFormResponse of(int totalPrice, List<CartOrderProductResponse> products) {
		return new CartOrderFormResponse(totalPrice, products);
	}

	public record CartOrderProductResponse(
		Long productId,
		String productName,
		String imageUrl,
		int productPrice,
		int quantity,
		int totalProductPrice,
		int deliveryFee
	) {

		public static CartOrderProductResponse of(
			Long productId, String productName, String imageUrl, int productPrice, int quantity,
			int totalProductPrice, int deliveryFee
		) {
			return new CartOrderProductResponse(
				productId, productName, imageUrl, productPrice, quantity, totalProductPrice, deliveryFee
			);
		}

		public static CartOrderProductResponse from(Product entity, int quantity, String imageUrl) {
			int price = entity.getPrice();

			return CartOrderProductResponse.of(
				entity.getId(),
				entity.getName(),
				imageUrl,
				price,
				quantity,
				price * quantity,
				entity.getDeliveryFee()
			);
		}
	}
}
