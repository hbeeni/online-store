package com.been.onlinestore.controller.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.dto.request.OrderServiceRequest;

public record OrderRequest(
	@NotNull @Positive
	Long productId,

	@NotNull @Positive(message = "한 개 이상의 상품을 주문해주세요.")
	Integer quantity,

	@NotEmpty @Size(max = 50)
	String deliveryAddress,

	@NotEmpty @Size(max = 20)
	String receiverName,

	@NotEmpty @Size(max = 20)
	@Pattern(regexp = "^010([0-9]{7,8})+$", message = "'-'(하이픈) 없이 10 ~ 11 자리의 숫자만 입력 가능합니다.")
	String receiverPhone
) {

	public OrderServiceRequest toServiceRequest() {
		return OrderServiceRequest.of(
			productId,
			quantity,
			deliveryAddress,
			receiverName,
			receiverPhone
		);
	}
}
