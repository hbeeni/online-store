package com.been.onlinestore.util;

import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.service.response.DeliveryStatusChangeResponse;
import com.been.onlinestore.service.response.OrderProductResponse;
import com.been.onlinestore.service.response.OrderResponse;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static java.time.LocalDateTime.now;

public class OrderTestDataUtil {

	public static Order createOrder(Long id) {
		Order order = Order.of(
				createUser(),
				DeliveryRequest.of("delivery address", "receiver name", "01012341234"),
				"01012341234",
				OrderStatus.ORDER
		);
		ReflectionTestUtils.setField(order, "id", id);
		return order;
	}

	public static OrderProduct createOrderProduct(Long id) {
		OrderProduct orderProduct = OrderProduct.of(
				createProduct(1L),
				10
		);
		ReflectionTestUtils.setField(orderProduct, "id", id);
		return orderProduct;
	}

	public static OrderResponse createOrderResponse(Long id, String uid, Long orderProductId) {
		return OrderResponse.of(
				id,
				OrderResponse.OrdererResponse.of(uid, "01011112222"),
				OrderResponse.DeliveryRequestResponse.of("address", "name", "01011112222"),
				List.of(createOrderProductResponse(orderProductId)),
				30000,
				OrderStatus.ORDER,
				now(),
				now()
		);
	}

	public static OrderProductResponse createOrderProductResponse(long id) {
		return OrderProductResponse.of(
				id,
				"product",
				3000,
				10,
				30000,
				DeliveryStatus.ACCEPT,
				3000,
				null
		);
	}

	public static DeliveryStatusChangeResponse createDeliveryStatusChangeResponse(Set<Long> succeededOrderProductIds) {
		return DeliveryStatusChangeResponse.of(
				null,
				succeededOrderProductIds,
				null
		);
	}
}
