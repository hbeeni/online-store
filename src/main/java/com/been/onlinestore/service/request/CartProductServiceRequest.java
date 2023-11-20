package com.been.onlinestore.service.request;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;

public record CartProductServiceRequest() {

    public record Create(
            Long productId,
            Integer productQuantity
    ) {

        public static Create of(Long productId, Integer productQuantity) {
            return new Create(productId, productQuantity);
        }

        public CartProduct toEntity(Cart cart, Product product) {
            return CartProduct.of(
                    cart,
                    product,
                    productQuantity
            );
        }
    }
}
