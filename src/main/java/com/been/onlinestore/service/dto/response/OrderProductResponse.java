package com.been.onlinestore.service.dto.response;

import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.file.ImageStore;

public record OrderProductResponse(
	Long orderProductId,
	String productName,
	int price,
	int quantity,
	int totalPrice,
	String imageUrl
) {

	public static OrderProductResponse of(
		Long orderProductId, String productName, int price, int quantity, int totalPrice, String imageUrl
	) {
		return new OrderProductResponse(orderProductId, productName, price, quantity, totalPrice, imageUrl);
	}

	public static OrderProductResponse from(OrderProduct entity, ImageStore imageStore) {
		return OrderProductResponse.of(
			entity.getId(),
			entity.getProduct().getName(),
			entity.getPrice(),
			entity.getQuantity(),
			entity.getTotalPrice(),
			imageStore.getImageUrl(entity.getProduct().getImageName())
		);
	}
}
