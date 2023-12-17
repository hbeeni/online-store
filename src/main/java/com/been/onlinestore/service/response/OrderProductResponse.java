package com.been.onlinestore.service.response;

import static com.been.onlinestore.service.response.JsonFormatConst.*;

import java.time.LocalDateTime;

import com.been.onlinestore.domain.Delivery;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.file.ImageStore;
import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderProductResponse(
	Long id,
	String productName,
	int price,
	int quantity,
	int totalPrice,
	DeliveryStatus deliveryStatus,
	int deliveryFee,
	String imageUrl,
	@JsonFormat(pattern = DATE_TIME_PATTERN) LocalDateTime deliveredAt
) {

	public static OrderProductResponse of(
		Long id, String productName, int price, int quantity, int totalPrice, DeliveryStatus deliveryStatus,
		int deliveryFee, String imageUrl, LocalDateTime deliveredAt
	) {
		return new OrderProductResponse(
			id, productName, price, quantity, totalPrice, deliveryStatus, deliveryFee, imageUrl, deliveredAt
		);
	}

	public static OrderProductResponse from(OrderProduct entity, ImageStore imageStore) {
		Delivery delivery = entity.getDelivery();

		return OrderProductResponse.of(
			entity.getId(),
			entity.getProduct().getName(),
			entity.getPrice(),
			entity.getQuantity(),
			entity.getTotalPrice(),
			delivery.getDeliveryStatus(),
			delivery.getDeliveryFee(),
			imageStore.getImageUrl(entity.getProduct().getImageName()),
			delivery.getDeliveredAt()
		);
	}
}
