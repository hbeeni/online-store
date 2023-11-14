package com.been.onlinestore.service;

import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderProductService {

    private final OrderProductRepository orderProductRepository;

    public void startPreparing(List<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);
        orderProducts.forEach(OrderProduct::startPreparing);
    }

    public void startDelivery(List<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);
        orderProducts.forEach(OrderProduct::startDelivery);
    }

    public void completeDelivery(List<Long> orderProductIds, Long sellerId) {
        List<OrderProduct> orderProducts = orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId);
        orderProducts.forEach(OrderProduct::completeDelivery);
    }
}
