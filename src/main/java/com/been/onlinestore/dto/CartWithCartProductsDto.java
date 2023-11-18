package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.service.request.UserServiceRequest;

import java.time.LocalDateTime;
import java.util.List;

public record CartWithCartProductsDto(
        Long id,
        UserServiceRequest userServiceRequest,
        List<CartProductDto> cartProductDtos,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static CartWithCartProductsDto of(Long id, UserServiceRequest userServiceRequest, List<CartProductDto> cartProductDtos, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new CartWithCartProductsDto(id, userServiceRequest, cartProductDtos, createdAt, modifiedAt);
    }

    public static CartWithCartProductsDto from(Cart entity) {
        return CartWithCartProductsDto.of(
                entity.getId(),
                null,
                entity.getCartProducts().stream()
                        .map(CartProductDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
