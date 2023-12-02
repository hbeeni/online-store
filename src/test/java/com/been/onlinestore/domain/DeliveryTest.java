package com.been.onlinestore.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.been.onlinestore.domain.constant.DeliveryStatus;

class DeliveryTest {

	@DisplayName("배송 상태가 ACCEPT일 때만 상품 준비를 시작할 수 있다.")
	@Test
	void startInstruct() {
		Delivery accept = Delivery.of(DeliveryStatus.ACCEPT, 3000, LocalDateTime.now());
		Delivery preparing = Delivery.of(DeliveryStatus.PREPARING, 3000, LocalDateTime.now());
		Delivery delivering = Delivery.of(DeliveryStatus.DELIVERING, 3000, LocalDateTime.now());
		Delivery finalDelivery = Delivery.of(DeliveryStatus.FINAL_DELIVERY, 3000, LocalDateTime.now());

		assertThat(accept.canStartPreparing()).isTrue();
		assertThat(preparing.canStartPreparing()).isFalse();
		assertThat(delivering.canStartPreparing()).isFalse();
		assertThat(finalDelivery.canStartPreparing()).isFalse();
	}

	@DisplayName("배송 상태가 PREPARING일 때만 배송을 시작할 수 있다.")
	@Test
	void startDelivery() {
		Delivery accept = Delivery.of(DeliveryStatus.ACCEPT, 3000, LocalDateTime.now());
		Delivery preparing = Delivery.of(DeliveryStatus.PREPARING, 3000, LocalDateTime.now());
		Delivery delivering = Delivery.of(DeliveryStatus.DELIVERING, 3000, LocalDateTime.now());
		Delivery finalDelivery = Delivery.of(DeliveryStatus.FINAL_DELIVERY, 3000, LocalDateTime.now());

		assertThat(accept.canStartDelivery()).isFalse();
		assertThat(preparing.canStartDelivery()).isTrue();
		assertThat(delivering.canStartDelivery()).isFalse();
		assertThat(finalDelivery.canStartDelivery()).isFalse();
	}

	@DisplayName("배송 상태가 DELIVERING일 때만 배송을 완료할 수 있다.")
	@Test
	void completeDelivery() {
		Delivery accept = Delivery.of(DeliveryStatus.ACCEPT, 3000, LocalDateTime.now());
		Delivery preparing = Delivery.of(DeliveryStatus.PREPARING, 3000, LocalDateTime.now());
		Delivery delivering = Delivery.of(DeliveryStatus.DELIVERING, 3000, LocalDateTime.now());
		Delivery finalDelivery = Delivery.of(DeliveryStatus.FINAL_DELIVERY, 3000, LocalDateTime.now());

		assertThat(accept.canCompleteDelivery()).isFalse();
		assertThat(preparing.canCompleteDelivery()).isFalse();
		assertThat(delivering.canCompleteDelivery()).isTrue();
		assertThat(finalDelivery.canCompleteDelivery()).isFalse();
	}
}
