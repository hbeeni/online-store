package com.been.onlinestore.service.response;

import static com.been.onlinestore.service.response.JsonFormatConst.*;

import java.time.LocalDateTime;
import java.util.List;

import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.file.ImageStore;
import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderResponse(
	Long id,
	OrdererResponse orderer,
	DeliveryRequestResponse deliveryRequest,
	List<OrderProductResponse> orderProducts,
	int totalPrice,
	OrderStatus orderStatus,
	@JsonFormat(pattern = DATE_TIME_PATTERN) LocalDateTime createdAt,
	@JsonFormat(pattern = DATE_TIME_PATTERN) LocalDateTime modifiedAt
) {

	public static OrderResponse of(
		Long id, OrdererResponse orderer, DeliveryRequestResponse deliveryRequest,
		List<OrderProductResponse> orderProducts,
		int totalPrice, OrderStatus orderStatus, LocalDateTime createdAt, LocalDateTime modifiedAt
	) {
		return new OrderResponse(
			id, orderer, deliveryRequest, orderProducts, totalPrice, orderStatus, createdAt, modifiedAt
		);
	}

	public static OrderResponse from(Order entity, ImageStore imageStore) {
		return OrderResponse.of(
			entity.getId(),
			OrdererResponse.from(entity.getOrderer()),
			DeliveryRequestResponse.from(entity.getDeliveryRequest()),
			entity.getOrderProducts().stream()
				.map(orderProduct -> OrderProductResponse.from(orderProduct, imageStore))
				.toList(),
			entity.getTotalPrice(),
			entity.getOrderStatus(),
			entity.getCreatedAt(),
			entity.getModifiedAt()
		);
	}

	public record OrdererResponse(
		String uid,
		String phone
	) {

		public static OrdererResponse of(String uid, String phone) {
			return new OrdererResponse(uid, phone);
		}

		public static OrdererResponse from(User entity) {
			return OrdererResponse.of(
				entity.getUid(),
				entity.getPhone()
			);
		}
	}

	public record DeliveryRequestResponse(
		String deliveryAddress,
		String receiverName,
		String receiverPhone
	) {

		public static DeliveryRequestResponse of(String deliveryAddress, String receiverName, String receiverPhone) {
			return new DeliveryRequestResponse(deliveryAddress, receiverName, receiverPhone);
		}

		public static DeliveryRequestResponse from(DeliveryRequest entity) {
			return DeliveryRequestResponse.of(
				entity.getDeliveryAddress(),
				entity.getReceiverName(),
				entity.getReceiverPhone()
			);
		}
	}
}
