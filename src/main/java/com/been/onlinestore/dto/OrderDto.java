package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(
        Long ordererId,
        String ordererPhone,
        DeliveryRequestDto deliveryRequestDto,
        List<OrderProductDto> orderProductDtos,
        Integer totalPrice,
        OrderStatus orderStatus,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static OrderDto of(Long ordererId, String ordererPhone, DeliveryRequestDto deliveryRequestDto, List<OrderProductDto> orderProductDtos, Integer totalPrice, OrderStatus orderStatus, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new OrderDto(ordererId, ordererPhone, deliveryRequestDto, orderProductDtos, totalPrice, orderStatus, createdAt, modifiedAt);
    }

    public static OrderDto from(Order entity) {
        return OrderDto.of(
                entity.getOrderer().getId(),
                entity.getOrdererPhone(),
                DeliveryRequestDto.from(entity.getDeliveryRequest()),
                entity.getOrderProducts().stream()
                        .map(OrderProductDto::from)
                        .toList(),
                entity.getTotalPrice(),
                entity.getOrderStatus(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
