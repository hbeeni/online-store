package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.Delivery;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.been.onlinestore.service.response.JsonFormatConst.DATE_TIME_PATTERN;

public record OrderProductResponse(
        Long id,
        String productName,
        int price,
        int quantity,
        int totalPrice,
        DeliveryStatus deliveryStatus,
        int deliveryFee,
        @JsonFormat(pattern = DATE_TIME_PATTERN) LocalDateTime deliveredAt
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
