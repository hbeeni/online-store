package com.been.onlinestore.service.dto.request;

public record OrderServiceRequest(
	Long productId,
	Integer quantity,
	String deliveryAddress,
	String receiverName,
	String receiverPhone
) {

	public static OrderServiceRequest of(
		Long productId, Integer quantity, String deliveryAddress, String receiverName, String receiverPhone
	) {
		return new OrderServiceRequest(productId, quantity, deliveryAddress, receiverName, receiverPhone);
	}
}
