package com.been.onlinestore.service;

import static com.been.onlinestore.service.dto.request.OrderServiceRequest.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.been.onlinestore.service.dto.request.OrderServiceRequest;
import com.been.onlinestore.service.dto.response.OrderResponse;

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

	public Long order(Long ordererId, OrderServiceRequest serviceRequest) {
		Map<Long, OrderProductServiceRequest> orderProductServiceRequestMap =
			createOrderProductServiceRequestMap(serviceRequest.orderProducts());

		List<Product> products = findProductsOnSale(orderProductServiceRequestMap.keySet());

		Order order = createOrder(ordererId, serviceRequest, products, orderProductServiceRequestMap);

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

	private Order createOrder(
		Long ordererId, OrderServiceRequest serviceRequest,
		List<Product> products, Map<Long, OrderProductServiceRequest> orderProductServiceRequestMap
	) {
		User orderer = userRepository.findById(ordererId)
			.orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_FOUND_USER.getMessage()));

		Order order = Order.create(
			orderer,
			DeliveryRequest.of(
				serviceRequest.deliveryAddress(),
				serviceRequest.receiverName(),
				serviceRequest.receiverPhone()
			),
			orderer.getPhone(),
			OrderStatus.ORDER,
			getDeliveryFee(products)
		);
		order.addOrderProducts(createOrderProducts(products, orderProductServiceRequestMap));

		return order;
	}

	private List<Product> findProductsOnSale(Set<Long> orderProductServiceRequestIds) {
		List<Product> products = productRepository.findAllOnSaleById(orderProductServiceRequestIds);

		if (orderProductServiceRequestIds.size() != products.size()) {
			throw new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage());
		}

		return products;
	}

	private static Map<Long, OrderProductServiceRequest> createOrderProductServiceRequestMap(
		List<OrderProductServiceRequest> orderProductServiceRequests
	) {
		return orderProductServiceRequests.stream()
			.collect(Collectors.toMap(
				OrderProductServiceRequest::id,
				orderProductServiceRequest -> orderProductServiceRequest)
			);
	}

	private static List<OrderProduct> createOrderProducts(
		List<Product> products,
		Map<Long, OrderProductServiceRequest> orderProductServiceRequestMap
	) {
		return products.stream()
			.map(product -> OrderProduct.create(
				product,
				orderProductServiceRequestMap.get(product.getId()).quantity()
			))
			.toList();
	}

	private static Integer getDeliveryFee(List<Product> products) {
		return products.stream()
			.map(Product::getDeliveryFee)
			.min(Comparator.naturalOrder())
			.orElse(3000);
	}
}
