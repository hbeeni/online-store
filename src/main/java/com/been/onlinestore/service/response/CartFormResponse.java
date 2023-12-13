package com.been.onlinestore.service.response;

import java.util.List;
import java.util.Map;

import com.been.onlinestore.domain.Product;

public record CartFormResponse(
	int totalPriceInCart,
	List<CartProductFormResponse> cartProducts
) {

	public static CartFormResponse of(int totalPriceInCart, List<CartProductFormResponse> cartProducts) {
		return new CartFormResponse(totalPriceInCart, cartProducts);
	}

	public record CartProductFormResponse(
		Long productId,
		String productName,
		String imageUrl,
		int productPrice,
		int quantity,
		int totalPrice,
		int deliveryFee
	) {

		public static CartProductFormResponse of(Long productId, String productName, String imageUrl, int productPrice,
			int quantity, int totalPrice, int deliveryFee) {
			return new CartProductFormResponse(
				productId, productName, imageUrl, productPrice, quantity, totalPrice, deliveryFee
			);
		}

		public static CartProductFormResponse from(Product entity, Map<Long, Integer> productToQuantityMap,
			String imageUrl) {
			int price = entity.getPrice();
			Integer quantity = productToQuantityMap.get(entity.getId());

			return CartProductFormResponse.of(
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
