package com.been.onlinestore.dto;

import com.been.onlinestore.domain.OrderProduct;

public record OrderProductDto(
        Long id,
        Long orderId,
        Long productId,
        Integer price,
        Integer quantity,
        Integer deliveryFee
) {

    public static OrderProductDto of(Long id, Long orderId, Long productId, Integer price, Integer quantity, Integer deliveryFee) {
        return new OrderProductDto(id, orderId, productId, price, quantity, deliveryFee);
    }

    public static OrderProductDto from(OrderProduct entity) {
        return OrderProductDto.of(
                entity.getId(),
                entity.getOrder().getId(),
                entity.getProduct().getId(),
                entity.getPrice(),
                entity.getQuantity(),
                entity.getDelivery().getDeliveryFee()
        );
    }
}
