package com.been.onlinestore.service.dto.request;

import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;

public record CartProductServiceRequest() {

	public record Create(Long productId, Integer productQuantity) {

		public static Create of(Long productId, Integer productQuantity) {
			return new Create(productId, productQuantity);
		}

		public CartProduct toEntity(User user, Product product) {
			return CartProduct.of(user, product, productQuantity);
		}
	}
}
