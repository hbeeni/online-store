package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.service.request.ProductServiceRequest;

public record CartProductDto(
        Long id,
        Long cartId,
        ProductServiceRequest productServiceRequest,
        Integer productQuantity
) {

    public static CartProductDto of(Long id, Long cartId, ProductServiceRequest productServiceRequest, Integer productQuantity) {
        return new CartProductDto(id, cartId, productServiceRequest, productQuantity);
    }

    public static CartProductDto from(CartProduct entity) {
        return CartProductDto.of(
                entity.getId(),
                entity.getCart().getId(),
                null,
                entity.getProductQuantity()
        );
    }

    public CartProduct toEntity(Cart cart, Product product) {
        return CartProduct.of(
                cart,
                product,
                productQuantity
        );
    }
}
