package com.been.onlinestore.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.enums.ErrorMessages;
import com.been.onlinestore.exception.CustomException;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.request.CartProductServiceRequest;
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
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_ORDER));
	}

	public Long order(Long ordererId, OrderServiceRequest serviceRequest) {
		Product product = productRepository.findOnSaleById(serviceRequest.productId())
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_PRODUCT));
		return order(ordererId, DeliveryRequestInfo.of(serviceRequest), product, serviceRequest.quantity());
	}

	Long order(Long ordererId, CartProductServiceRequest.Order serviceRequest, Map<Long, Integer> orderProductMap) {
		List<Product> products = findProductsOnSale(orderProductMap.keySet());
		return order(ordererId, DeliveryRequestInfo.of(serviceRequest), products, orderProductMap);
	}

	public Long cancelOrder(Long orderId, Long ordererId) {
		Order order = orderRepository.findByIdAndOrdererId(orderId, ordererId)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_ORDER));
		if (order.getOrderStatus() == OrderStatus.ORDER) {
			order.cancel();
		}
		return order.getId();
	}

	private Long order(Long ordererId, DeliveryRequestInfo deliveryRequestInfo, Product product, int quantity) {
		return order(ordererId, deliveryRequestInfo, List.of(product), Map.of(product.getId(), quantity));
	}

	private Long order(
		Long ordererId, DeliveryRequestInfo deliveryRequestInfo,
		List<Product> products, Map<Long, Integer> orderProductMap
	) {
		User orderer = userRepository.findById(ordererId)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_USER));

		Order order = Order.create(
			orderer,
			DeliveryRequest.of(
				deliveryRequestInfo.deliveryAddress(),
				deliveryRequestInfo.receiverName(),
				deliveryRequestInfo.receiverPhone()
			),
			orderer.getPhone(),
			OrderStatus.ORDER,
			getDeliveryFee(products)
		);
		order.setOrderProducts(createOrderProducts(products, orderProductMap));

		return orderRepository.save(order).getId();
	}

	private List<OrderProduct> createOrderProducts(List<Product> products, Map<Long, Integer> productIdToQuantityMap) {
		return products.stream()
			.map(product -> OrderProduct.create(
				product,
				productIdToQuantityMap.get(product.getId())
			))
			.toList();
	}

	private List<Product> findProductsOnSale(Set<Long> productIds) {
		List<Product> products = productRepository.findAllOnSaleById(productIds);

		if (productIds.size() != products.size()) {
			throw new CustomException(ErrorMessages.NOT_FOUND_PRODUCT);
		}

		return products;
	}

	private static Integer getDeliveryFee(List<Product> products) {
		return products.stream()
			.map(Product::getDeliveryFee)
			.min(Comparator.naturalOrder())
			.orElse(3000);
	}

	private record DeliveryRequestInfo(String deliveryAddress, String receiverName, String receiverPhone) {
		private static DeliveryRequestInfo of(OrderServiceRequest serviceRequest) {
			return new DeliveryRequestInfo(
				serviceRequest.deliveryAddress(),
				serviceRequest.receiverName(),
				serviceRequest.receiverPhone()
			);
		}

		private static DeliveryRequestInfo of(CartProductServiceRequest.Order serviceRequest) {
			return new DeliveryRequestInfo(
				serviceRequest.deliveryAddress(),
				serviceRequest.receiverName(),
				serviceRequest.receiverPhone()
			);
		}
	}
}
