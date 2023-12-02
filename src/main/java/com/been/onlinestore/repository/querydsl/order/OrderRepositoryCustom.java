package com.been.onlinestore.repository.querydsl.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.been.onlinestore.domain.Order;

public interface OrderRepositoryCustom {

	Page<Order> findAllOrdersByOrderer(Long ordererId, Pageable pageable);

	Optional<Order> findOrderByOrderer(Long orderId, Long ordererId);

	Page<Order> findAllOrdersBySeller(Long sellerId, Pageable pageable);

	Optional<Order> findOrderBySeller(Long orderId, Long sellerId);
}
