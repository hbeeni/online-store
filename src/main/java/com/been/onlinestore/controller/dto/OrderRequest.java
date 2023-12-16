package com.been.onlinestore.controller.dto;

import static com.been.onlinestore.service.request.OrderServiceRequest.*;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.request.OrderServiceRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

	@NotNull
	private List<OrderProductRequest> orderProducts;

	@NotEmpty
	@Size(max = 50)
	private String deliveryAddress;

	@NotEmpty
	@Size(max = 20)
	private String receiverName;

	@NotEmpty
	@Size(max = 20)
	@Pattern(regexp = "^010([0-9]{7,8})+$", message = "'-'(하이픈) 없이 10 ~ 11 자리의 숫자만 입력 가능합니다.")
	private String receiverPhone;

	public OrderServiceRequest toServiceRequest() {
		return of(
			orderProducts.stream()
				.map(OrderProductRequest::toServiceRequest)
				.toList(),
			deliveryAddress,
			receiverName,
			receiverPhone
		);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderProductRequest {

		@NotNull
		@Positive
		private Long id;

		@NotNull
		@Positive(message = "한 개 이상의 상품을 주문해주세요.")
		private Integer quantity;

		public OrderProductServiceRequest toServiceRequest() {
			return OrderProductServiceRequest.of(id, quantity);
		}
	}
}
