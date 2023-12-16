package com.been.onlinestore.service;

import static com.been.onlinestore.service.request.OrderServiceRequest.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.repository.querydsl.order.OrderSearchCondition;
import com.been.onlinestore.service.request.OrderServiceRequest;
import com.been.onlinestore.service.response.OrderResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final ImageStore imageStore;

	@Transactional(readOnly = true)
	public Page<OrderResponse> findOrdersByOrderer(Long ordererId, Pageable pageable) {
		return orderRepository.findAllOrdersByOrderer(ordererId, pageable)
			.map(order -> OrderResponse.from(order, imageStore));
	}

	@Transactional(readOnly = true)
	public OrderResponse findOrderByOrderer(Long orderId, Long ordererId) {
		return orderRepository.findOrderByOrderer(orderId, ordererId)
			.map(order -> OrderResponse.from(order, imageStore))
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> findOrdersBySeller(Long sellerId, OrderSearchCondition cond, Pageable pageable) {
		return orderRepository.searchOrdersBySeller(sellerId, cond, pageable)
			.map(order -> OrderResponse.from(order, imageStore));
	}

	@Transactional(readOnly = true)
	public OrderResponse findOrderBySeller(Long orderId, Long sellerId) {
		return orderRepository.findOrderBySeller(orderId, sellerId)
			.map(order -> OrderResponse.from(order, imageStore))
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
	}

	public Long order(Long ordererId, OrderServiceRequest serviceRequest) {
		List<OrderProductServiceRequest> orderProductServiceRequests = serviceRequest.orderProducts();
		Map<Long, OrderProductServiceRequest> orderProductServiceRequestMap =
			createIdToOrderProductServiceRequestMap(orderProductServiceRequests);

		List<Product> products = productRepository.findAllOnSaleById(orderProductServiceRequestMap.keySet());
		if (orderProductServiceRequestMap.keySet().size() != products.size()) {
			throw new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage());
		}

		List<OrderProduct> orderProducts = createOrderProducts(products, orderProductServiceRequestMap);

		User orderer = userRepository.getReferenceById(ordererId);
		Order order = Order.of(
			orderer,
			DeliveryRequest.of(
				serviceRequest.deliveryAddress(),
				serviceRequest.receiverName(),
				serviceRequest.receiverPhone()
			),
			orderer.getPhone(),
			OrderStatus.ORDER
		);
		order.addOrderProducts(orderProducts);
		return orderRepository.save(order).getId();
	}

	public Long cancelOrder(Long orderId, Long ordererId) {
		Order order = orderRepository.findByIdAndOrdererId(orderId, ordererId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
		if (order.getOrderStatus() == OrderStatus.ORDER) {
			order.cancel();
		}
		return order.getId();
	}

	private static Map<Long, OrderProductServiceRequest> createIdToOrderProductServiceRequestMap(
		List<OrderProductServiceRequest> orderProductServiceRequests
	) {
		return orderProductServiceRequests.stream()
			.collect(Collectors.toMap(
				OrderProductServiceRequest::id,
				orderProductServiceRequest -> orderProductServiceRequest)
			);
	}

	private List<OrderProduct> createOrderProducts(
		List<Product> products,
		Map<Long, OrderProductServiceRequest> orderProductServiceRequestMap
	) {
		return products.stream()
			.map(product -> OrderProduct.of(
				product,
				orderProductServiceRequestMap.get(product.getId()).quantity())
			)
			.toList();
	}
}
