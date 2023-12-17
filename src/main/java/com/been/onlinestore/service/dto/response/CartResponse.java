package com.been.onlinestore.service.dto.response;

import java.util.List;
import java.util.Map;

import com.been.onlinestore.domain.Product;

public record CartResponse(
	int totalPrice,
	int deliveryFee,
	List<CartProductResponse> cartProducts
) {

	public static CartResponse of(int totalPrice, int deliveryFee, List<CartProductResponse> cartProducts) {
		return new CartResponse(totalPrice, deliveryFee, cartProducts);
	}

	public static CartResponse from(List<Product> entities, Map<Long, Integer> productToQuantityMap, int deliveryFee) {
		List<CartProductResponse> cartProducts = entities.stream()
			.map(entity -> CartProductResponse.from(entity, productToQuantityMap.get(entity.getId())))
			.toList();

		return CartResponse.of(
			cartProducts.stream().mapToInt(CartProductResponse::totalPrice).sum(),
			deliveryFee,
			cartProducts
		);
	}

	public record CartProductResponse(
		Long productId,
		String productName,
		int productPrice,
		int quantity,
		int totalPrice
	) {

		public static CartProductResponse of(Long productId, String productName, int productPrice, int quantity,
			int totalPrice) {
			return new CartProductResponse(productId, productName, productPrice, quantity, totalPrice);
		}

		public static CartProductResponse from(Product entity, int quantity) {
			int price = entity.getPrice();

			return CartProductResponse.of(
				entity.getId(),
				entity.getName(),
				price,
				quantity,
				price * quantity
			);
		}
	}
}
