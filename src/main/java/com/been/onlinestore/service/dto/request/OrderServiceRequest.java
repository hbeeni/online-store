package com.been.onlinestore.service.dto.request;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record OrderServiceRequest(
	List<OrderProductServiceRequest> orderProducts,
	String deliveryAddress,
	String receiverName,
	String receiverPhone
) {

	public static OrderServiceRequest of(
		List<OrderProductServiceRequest> orderProducts,
		String deliveryAddress, String receiverName, String receiverPhone
	) {
		return new OrderServiceRequest(orderProducts, deliveryAddress, receiverName, receiverPhone);
	}

	public record OrderProductServiceRequest(
		@NotNull @Positive
		Long id,

		@NotNull @Positive(message = "한 개 이상의 상품을 주문해주세요.")
		Integer quantity
	) {

		public static OrderProductServiceRequest of(Long id, Integer quantity) {
			return new OrderProductServiceRequest(id, quantity);
		}
	}
}
