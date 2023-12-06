package com.been.onlinestore.repository.querydsl.order;

import javax.validation.constraints.Positive;

import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;

public record OrderSearchCondition(
	@Positive
	Long ordererId,

	@Positive
	Long productId,

	DeliveryStatus deliveryStatus,

	OrderStatus orderStatus
) {

	public static OrderSearchCondition of(
		Long ordererId, Long productId, DeliveryStatus deliveryStatus, OrderStatus orderStatus
	) {
		return new OrderSearchCondition(ordererId, productId, deliveryStatus, orderStatus);
	}
}
