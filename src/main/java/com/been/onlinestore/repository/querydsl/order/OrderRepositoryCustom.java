package com.been.onlinestore.repository.querydsl.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.been.onlinestore.domain.Order;

public interface OrderRepositoryCustom {

	Page<Order> findAllOrdersByOrderer(Long ordererId, Pageable pageable);

	Optional<Order> findOrderByOrderer(Long orderId, Long ordererId);

	Page<Order> findOrdersForAdmin(OrderSearchCondition cond, Pageable pageable);

	Optional<Order> findOrderByIdForAdmin(Long orderId);
}
