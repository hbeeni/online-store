package com.been.onlinestore.util;

import static com.been.onlinestore.util.UserTestDataUtil.*;

import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.domain.Delivery;
import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;

public class OrderTestDataUtil {

	public static Order createOrder(Long id) {
		Order order = Order.create(
			createUser(),
			DeliveryRequest.of("delivery address", "receiver name", "01012341234"),
			"01012341234",
			OrderStatus.ORDER,
			3000
		);
		ReflectionTestUtils.setField(order, "id", id);
		return order;
	}

	public static Order createOrder(Long id, DeliveryStatus deliveryStatus) {
		Order order = Order.create(
			createUser(),
			DeliveryRequest.of("delivery address", "receiver name", "01012341234"),
			"01012341234",
			OrderStatus.ORDER,
			3000
		);

		Delivery delivery = Delivery.of(deliveryStatus, 3000, null);

		ReflectionTestUtils.setField(order, "id", id);
		ReflectionTestUtils.setField(order, "delivery", delivery);
		return order;
	}
}
