package com.been.onlinestore.service.request;

import java.util.Map;

public record OrderServiceRequest(
        Map<Long, Integer> productIdToQuantityMap,
        String deliveryAddress,
        String receiverName,
        String receiverPhone
) {

    public static OrderServiceRequest of(Map<Long, Integer> productIdToQuantityMap, String deliveryAddress, String receiverName, String receiverPhone) {
        return new OrderServiceRequest(productIdToQuantityMap, deliveryAddress, receiverName, receiverPhone);
    }
}
