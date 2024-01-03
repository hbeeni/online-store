package com.been.onlinestore.service.dto.response;

import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;

public record CartProductResponse(
	Long cartProductId,
	String productName,
	int productPrice,
	int quantity,
	int totalPrice
) {

	public static CartProductResponse of(
		Long cartProductId, String productName, int productPrice, int quantity, int totalPrice
	) {
		return new CartProductResponse(cartProductId, productName, productPrice, quantity, totalPrice);
	}

	public static CartProductResponse from(CartProduct entity) {
		Product product = entity.getProduct();
		return CartProductResponse.of(
			entity.getId(),
			product.getName(),
			product.getPrice(),
			entity.getQuantity(),
			entity.getTotalPrice()
		);
	}
}
