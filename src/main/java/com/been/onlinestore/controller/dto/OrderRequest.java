package com.been.onlinestore.controller.dto;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.request.OrderServiceRequest;

public record OrderRequest(
		@NotNull
		Map<@Positive Long, @Positive Integer> productIdToQuantity,

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
				productIdToQuantity,
				deliveryAddress,
				receiverName,
				receiverPhone
		);
	}
}
