package com.been.onlinestore.service.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.domain.Order;
import com.been.onlinestore.enums.ErrorMessages;
import com.been.onlinestore.exception.CustomException;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.querydsl.order.OrderSearchCondition;
import com.been.onlinestore.service.dto.response.OrderResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class AdminOrderService {

	private final OrderRepository orderRepository;
	private final ImageStore imageStore;

	@Transactional(readOnly = true)
	public Page<OrderResponse> findOrders(OrderSearchCondition cond, Pageable pageable) {
		return orderRepository.findOrdersForAdmin(cond, pageable)
			.map(order -> OrderResponse.from(order, imageStore));
	}

	@Transactional(readOnly = true)
	public OrderResponse findOrder(Long orderId) {
		return orderRepository.findOrderByIdForAdmin(orderId)
			.map(order -> OrderResponse.from(order, imageStore))
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_ORDER));
	}

	public Long startPreparing(Long orderId) {
		Order order = orderRepository.findByIdWithDetails(orderId)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_ORDER));
		order.startPreparing();
		return orderId;
	}

	public Long startDelivery(Long orderId) {
		Order order = orderRepository.findByIdWithDetails(orderId)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_ORDER));
		order.startDelivery();
		return orderId;
	}

	public Long completeDelivery(Long orderId) {
		Order order = orderRepository.findByIdWithDetails(orderId)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_ORDER));
		order.completeDelivery();
		return orderId;
	}

	public Long cancelOrder(Long orderId) {
		Order order = orderRepository.findByIdWithDetails(orderId)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_ORDER));
		order.cancel();
		return orderId;
	}
}
