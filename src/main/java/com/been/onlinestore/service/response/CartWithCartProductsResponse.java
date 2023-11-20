package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.Cart;

import java.util.List;

public record CartWithCartProductsResponse(
        Long cartId,
        int totalPrice,
        List<CartProductResponse> cartProducts
) {

    public static CartWithCartProductsResponse of(Long cartId, int totalPrice, List<CartProductResponse> cartProducts) {
        return new CartWithCartProductsResponse(cartId, totalPrice, cartProducts);
    }

    public static CartWithCartProductsResponse from(Cart entity) {
        return CartWithCartProductsResponse.of(
                entity.getId(),
                entity.getTotalPrice(),
                entity.getCartProducts().stream()
                        .map(CartProductResponse::from)
                        .toList()
        );
    }
}
