package com.been.onlinestore.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {

	ACCEPT("결제 완료"),
	PREPARING("상품 준비중"),
	DELIVERING("배송 중"),
	COMPLETED("배송 완료");

	private final String description;
}
