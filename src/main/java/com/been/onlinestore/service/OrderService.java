package com.been.onlinestore.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
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

	@Transactional(readOnly = true)
	public Page<OrderResponse> findOrdersByOrderer(Long ordererId, Pageable pageable) {
		return orderRepository.findAllOrdersByOrderer(ordererId, pageable)
				.map(OrderResponse::from);
	}

	@Transactional(readOnly = true)
	public OrderResponse findOrderByOrderer(Long orderId, Long ordererId) {
		return orderRepository.findOrderByOrderer(orderId, ordererId)
				.map(OrderResponse::from)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> findOrdersBySeller(Long sellerId, Pageable pageable) {
		return orderRepository.findAllOrdersBySeller(sellerId, pageable)
				.map(OrderResponse::from);
	}

	@Transactional(readOnly = true)
	public OrderResponse findOrderBySeller(Long orderId, Long sellerId) {
		return orderRepository.findOrderBySeller(orderId, sellerId)
				.map(OrderResponse::from)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
	}

	public Long order(Long ordererId, OrderServiceRequest serviceRequest) {
		Map<Long, Integer> productIdToQuantityMap = serviceRequest.productIdToQuantityMap();

		List<Product> products = productRepository.findAllOnSaleById(productIdToQuantityMap.keySet());
		if (productIdToQuantityMap.keySet().size() != products.size()) {
			throw new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage());
		}

		Map<Product, Integer> productToQuantityMap = convertToProductToQuantityMap(productIdToQuantityMap, products);
		List<OrderProduct> orderProducts = createOrderProducts(productToQuantityMap);

		User orderer = userRepository.getReferenceById(ordererId);
		Order order = Order.of(
				orderer,
				DeliveryRequest.of(serviceRequest.deliveryAddress(), serviceRequest.receiverName(),
						serviceRequest.receiverPhone()),
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

	private Map<Product, Integer> convertToProductToQuantityMap(Map<Long, Integer> productIdToQuantityMap,
			List<Product> products) {
		return IntStream.range(0, products.size())
				.boxed()
				.collect(Collectors.toMap(
						products::get,
						i -> productIdToQuantityMap.get(products.get(i).getId()))
				);
	}

	private List<OrderProduct> createOrderProducts(Map<Product, Integer> productToQuantityMap) {
		return productToQuantityMap.entrySet().stream()
				.map(entry -> OrderProduct.of(
								entry.getKey(),
								entry.getValue()
						)
				)
				.toList();
	}
}
