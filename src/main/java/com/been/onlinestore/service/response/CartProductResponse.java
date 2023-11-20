package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;

public record CartProductResponse(
        Long cartProductId,
        Long productId,
        String productName,
        int productPrice,
        int quantity,
        int totalPrice,
        int deliveryFee
) {

    public static CartProductResponse of(Long cartProductId, Long productId, String productName, int productPrice, int quantity, int totalPrice, int deliveryFee) {
        return new CartProductResponse(cartProductId, productId, productName, productPrice, quantity, totalPrice, deliveryFee);
    }

    public static CartProductResponse from(CartProduct entity) {
        Product product = entity.getProduct();
        return CartProductResponse.of(
                entity.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                entity.getProductQuantity(),
                entity.getTotalPrice(),
                product.getDeliveryFee()
        );
    }
}
