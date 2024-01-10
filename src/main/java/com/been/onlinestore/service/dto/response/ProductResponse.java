package com.been.onlinestore.service.dto.response;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;

public record ProductResponse(
	Long id,
	String name,
	int price,
	String description,
	SaleStatus saleStatus,
	int deliveryFee,
	String imageUrl
) {

	public static ProductResponse of(
		Long id, String name, int price, String description, SaleStatus saleStatus, int deliveryFee, String imageUrl
	) {
		return new ProductResponse(id, name, price, description, saleStatus, deliveryFee, imageUrl);
	}

	public static ProductResponse from(Product entity, String imageUrl) {
		return ProductResponse.of(
			entity.getId(),
			entity.getName(),
			entity.getPrice(),
			entity.getDescription(),
			entity.getSaleStatus(),
			entity.getDeliveryFee(),
			imageUrl
		);
	}
}
