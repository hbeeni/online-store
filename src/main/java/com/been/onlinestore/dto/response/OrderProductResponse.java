package com.been.onlinestore.dto.response;

import com.been.onlinestore.domain.Delivery;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.DeliveryStatus;

import java.time.LocalDateTime;

public record OrderProductResponse(
        Long id,
        String productName,
        int price,
        int quantity,
        int totalPrice,
        DeliveryStatus deliveryStatus,
        int deliveryFee,
        LocalDateTime deliveredAt
) {

    public static OrderProductResponse of(Long id, String productName, int price, int quantity, int totalPrice, DeliveryStatus deliveryStatus, int deliveryFee, LocalDateTime deliveredAt) {
        return new OrderProductResponse(id, productName, price, quantity, totalPrice, deliveryStatus, deliveryFee, deliveredAt);
    }

    public static OrderProductResponse from(OrderProduct entity) {
        Delivery delivery = entity.getDelivery();

        return OrderProductResponse.of(
                entity.getId(),
                entity.getProduct().getName(),
                entity.getPrice(),
                entity.getQuantity(),
                entity.getTotalPrice(),
                delivery.getDeliveryStatus(),
                delivery.getDeliveryFee(),
                delivery.getDeliveredAt()
        );
    }
}
