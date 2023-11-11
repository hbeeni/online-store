package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Cart;

import java.time.LocalDateTime;
import java.util.List;

public record CartWithCartProductsDto(
        Long id,
        UserDto userDto,
        List<CartProductDto> cartProductDtos,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static CartWithCartProductsDto of(Long id, UserDto userDto, List<CartProductDto> cartProductDtos, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new CartWithCartProductsDto(id, userDto, cartProductDtos, createdAt, modifiedAt);
    }

    public static CartWithCartProductsDto from(Cart entity) {
        return CartWithCartProductsDto.of(
                entity.getId(),
                UserDto.from(entity.getUser()),
                entity.getCartProducts().stream()
                        .map(CartProductDto::from)
                        .toList(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
