package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.Product;

public record ProductResponse(
	Long id,
	String name,
	int price,
	String description,
	int deliveryFee,
	String imageUrl
) {

	public static ProductResponse of(Long id, String name, int price, String description, int deliveryFee,
		String imageUrl) {
		return new ProductResponse(id, name, price, description, deliveryFee, imageUrl);
	}

	public static ProductResponse from(Product entity, String imageUrl) {
		return ProductResponse.of(
			entity.getId(),
			entity.getName(),
			entity.getPrice(),
			entity.getDescription(),
			entity.getDeliveryFee(),
			imageUrl
		);
	}
}
