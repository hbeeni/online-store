package com.been.onlinestore.util;

import static com.been.onlinestore.util.ProductTestDataUtil.*;
import static com.been.onlinestore.util.UserTestDataUtil.*;

import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.OrderStatus;

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
}
