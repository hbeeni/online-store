package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;

public record CartProductDto(
        Long id,
        Long cartId,
        ProductDto productDto,
        Integer productQuantity
) {

    public static CartProductDto of(Long id, Long cartId, ProductDto productDto, Integer productQuantity) {
        return new CartProductDto(id, cartId, productDto, productQuantity);
    }

    public static CartProductDto from(CartProduct entity) {
        return CartProductDto.of(
                entity.getId(),
                entity.getCart().getId(),
                ProductDto.from(entity.getProduct()),
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
