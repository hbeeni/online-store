package com.been.onlinestore.util;

import static com.been.onlinestore.util.UserTestDataUtil.*;

import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.constant.OrderStatus;

public class OrderTestDataUtil {

	public static Order createOrder(Long id) {
		Order order = Order.of(
			createUser(),
			DeliveryRequest.of("delivery address", "receiver name", "01012341234"),
			"01012341234",
			OrderStatus.ORDER,
			3000
		);
		ReflectionTestUtils.setField(order, "id", id);
		return order;
	}
}
