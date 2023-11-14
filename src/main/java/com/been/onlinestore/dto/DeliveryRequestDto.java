package com.been.onlinestore.dto;

import com.been.onlinestore.domain.DeliveryRequest;

public record DeliveryRequestDto(
        Long id,
        String deliveryAddress,
        String receiverName,
        String receiverPhone
) {

    public static DeliveryRequestDto of(Long id, String deliveryAddress, String receiverName, String receiverPhone) {
        return new DeliveryRequestDto(id, deliveryAddress, receiverName, receiverPhone);
    }

    public static DeliveryRequestDto from(DeliveryRequest entity) {
        return DeliveryRequestDto.of(
                entity.getId(),
                entity.getDeliveryAddress(),
                entity.getReceiverName(),
                entity.getReceiverPhone()
        );
    }
}
