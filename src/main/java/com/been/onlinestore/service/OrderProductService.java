package com.been.onlinestore.service;

import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.repository.OrderProductRepository;
import com.been.onlinestore.service.response.DeliveryStatusChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public DeliveryStatusChangeResponse startPreparing(Set<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> foundOrderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);

        Set<OrderProduct> succeededOrderProducts = foundOrderProducts.stream()
                .filter(OrderProduct::canStartPreparing)
                .collect(Collectors.toSet());
        Set<Long> succeededOrderProductIds = getSucceededOrderProductIds(succeededOrderProducts);

        orderProductRepository.bulkStartPreparing(succeededOrderProductIds);

        return getDeliveryStatusChangeResponse(orderProductIds, foundOrderProducts, succeededOrderProductIds);
    }

    public DeliveryStatusChangeResponse startDelivery(Set<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> foundOrderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);

        Set<OrderProduct> succeededOrderProducts = foundOrderProducts.stream()
                .filter(OrderProduct::canStartDelivery)
                .collect(Collectors.toSet());
        Set<Long> succeededOrderProductIds = getSucceededOrderProductIds(succeededOrderProducts);

        orderProductRepository.bulkStartDelivery(succeededOrderProductIds);

        return getDeliveryStatusChangeResponse(orderProductIds, foundOrderProducts, succeededOrderProductIds);
    }

    public DeliveryStatusChangeResponse completeDelivery(Set<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> foundOrderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);

        Set<OrderProduct> succeededOrderProducts = foundOrderProducts.stream()
                .filter(OrderProduct::canCompleteDelivery)
                .collect(Collectors.toSet());
        Set<Long> succeededOrderProductIds = getSucceededOrderProductIds(succeededOrderProducts);

        orderProductRepository.bulkCompleteDelivery(succeededOrderProductIds);

        return getDeliveryStatusChangeResponse(orderProductIds, foundOrderProducts, succeededOrderProductIds);
    }

    private static DeliveryStatusChangeResponse getDeliveryStatusChangeResponse(
            Set<Long> orderProductIds,
            List<OrderProduct> foundOrderProducts,
            Set<Long> succeededOrderProductIds
    ) {
        Set<Long> foundOrderProductIds = getFoundOrderProductIds(foundOrderProducts);
        Set<Long> notFoundOrderProductIds = getNotFoundOrderProductIds(orderProductIds, foundOrderProductIds);
        Set<Long> failedOrderProductIds = getFailedOrderProductIds(foundOrderProductIds, succeededOrderProductIds);

        return DeliveryStatusChangeResponse.of(notFoundOrderProductIds, succeededOrderProductIds, failedOrderProductIds);
    }

    private static Set<Long> getNotFoundOrderProductIds(Set<Long> orderProductIds, Set<Long> foundOrderProductIds) {
        return orderProductIds.stream()
                .filter(orderProductId -> !foundOrderProductIds.contains(orderProductId))
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Set<Long> getFoundOrderProductIds(List<OrderProduct> foundOrderProducts) {
        return foundOrderProducts.stream()
                .map(OrderProduct::getId)
                .collect(Collectors.toSet());
    }

    private static Set<Long> getSucceededOrderProductIds(Set<OrderProduct> succeededOrderProducts) {
        return succeededOrderProducts.stream()
                .map(OrderProduct::getId)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static Set<Long> getFailedOrderProductIds(Set<Long> foundOrderProductIds, Set<Long> succeededOrderProductIds) {
        return foundOrderProductIds.stream()
                .filter(orderProductId -> !succeededOrderProductIds.contains(orderProductId))
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
