package com.been.onlinestore.service.dto.response;

import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;

public record CartProductResponse(
	Long cartProductId,
	String productName,
	int productPrice,
	int quantity,
	int totalPrice,
	SaleStatus saleStatus
) {

	public static CartProductResponse of(
		Long cartProductId, String productName, int productPrice, int quantity, int totalPrice, SaleStatus saleStatus
	) {
		return new CartProductResponse(cartProductId, productName, productPrice, quantity, totalPrice, saleStatus);
	}

	public static CartProductResponse from(CartProduct entity) {
		Product product = entity.getProduct();
		return CartProductResponse.of(
			entity.getId(),
			product.getName(),
			product.getPrice(),
			entity.getQuantity(),
			entity.getTotalPrice(),
			entity.getProduct().getSaleStatus()
		);
	}
}
