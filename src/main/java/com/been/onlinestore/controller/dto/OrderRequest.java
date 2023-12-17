package com.been.onlinestore.controller.dto;

import static com.been.onlinestore.service.dto.request.OrderServiceRequest.*;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.dto.request.OrderServiceRequest;

public record OrderRequest(
	@NotNull
	List<OrderProductRequest> orderProducts,

	@NotEmpty
	@Size(max = 50)
	String deliveryAddress,

	@NotEmpty
	@Size(max = 20)
	String receiverName,

	@NotEmpty
	@Size(max = 20)
	@Pattern(regexp = "^010([0-9]{7,8})+$", message = "'-'(하이픈) 없이 10 ~ 11 자리의 숫자만 입력 가능합니다.")
	String receiverPhone
) {

	public OrderServiceRequest toServiceRequest() {
		return OrderServiceRequest.of(
			orderProducts.stream()
				.map(OrderProductRequest::toServiceRequest)
				.toList(),
			deliveryAddress,
			receiverName,
			receiverPhone
		);
	}

	public record OrderProductRequest(
		@NotNull
		@Positive
		Long id,

		@NotNull
		@Positive(message = "한 개 이상의 상품을 주문해주세요.")
		Integer quantity
	) {

		public OrderProductServiceRequest toServiceRequest() {
			return OrderProductServiceRequest.of(id, quantity);
		}
	}
}
