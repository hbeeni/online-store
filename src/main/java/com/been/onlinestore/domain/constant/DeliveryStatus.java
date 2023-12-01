package com.been.onlinestore.domain.constant;

public enum DeliveryStatus {

	ACCEPT("결제 완료"),
	PREPARING("상품 준비중"),
	DELIVERING("배송 중"),
	FINAL_DELIVERY("배송 완료");

	private final String description;

	DeliveryStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
