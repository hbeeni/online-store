package com.been.onlinestore.service;

import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public void startPreparing(Set<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);
        orderProducts.forEach(OrderProduct::startPreparing);
    }

    public void startDelivery(Set<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);
        orderProducts.forEach(OrderProduct::startDelivery);
    }

    public void completeDelivery(Set<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);
        orderProducts.forEach(OrderProduct::completeDelivery);
    }
}
