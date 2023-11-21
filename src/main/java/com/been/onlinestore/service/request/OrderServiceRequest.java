package com.been.onlinestore.service.request;

import com.been.onlinestore.domain.constant.OrderStatus;

import java.util.Map;
import java.util.Set;

public record OrderServiceRequest() {

    public record Create(
            Map<Long, Integer> productIdToQuantityMap,
            String deliveryAddress,
            String receiverName,
            String receiverPhone
    ) {}

    public record Update(
            Set<Long> orderIds,
            OrderStatus orderStatus
    ) {}
}
