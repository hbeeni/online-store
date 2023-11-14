package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.DeliveryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeliveryTest {

    @DisplayName("배송 상태가 ACCEPT일 때만 상품 준비를 시작할 수 있다.")
    @Test
    void startInstruct() {
        Delivery deliveryAccept = Delivery.of(DeliveryStatus.ACCEPT, 3000, LocalDateTime.now());
        Delivery deliveryInstruct = Delivery.of(DeliveryStatus.PREPARING, 3000, LocalDateTime.now());
        Delivery deliveryDelivering = Delivery.of(DeliveryStatus.DELIVERING, 3000, LocalDateTime.now());
        Delivery deliveryFinalDelivery = Delivery.of(DeliveryStatus.FINAL_DELIVERY, 3000, LocalDateTime.now());

        deliveryAccept.startPreparing();

        assertThat(deliveryAccept.getDeliveryStatus()).isEqualTo(DeliveryStatus.PREPARING);
        assertThatThrownBy(deliveryInstruct::startPreparing).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(deliveryDelivering::startPreparing).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(deliveryFinalDelivery::startPreparing).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배송 상태가 INSTRUCT일 때만 배송을 시작할 수 있다.")
    @Test
    void startDelivery() {
        Delivery deliveryAccept = Delivery.of(DeliveryStatus.ACCEPT, 3000, LocalDateTime.now());
        Delivery deliveryInstruct = Delivery.of(DeliveryStatus.PREPARING, 3000, LocalDateTime.now());
        Delivery deliveryDelivering = Delivery.of(DeliveryStatus.DELIVERING, 3000, LocalDateTime.now());
        Delivery deliveryFinalDelivery = Delivery.of(DeliveryStatus.FINAL_DELIVERY, 3000, LocalDateTime.now());

        deliveryInstruct.startDelivery();

        assertThatThrownBy(deliveryAccept::startDelivery).isInstanceOf(IllegalArgumentException.class);
        assertThat(deliveryInstruct.getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERING);
        assertThatThrownBy(deliveryDelivering::startDelivery).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(deliveryFinalDelivery::startDelivery).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배송 상태가 DELIVERING일 때만 배송을 완료할 수 있다.")
    @Test
    void completeDelivery() {
        Delivery deliveryAccept = Delivery.of(DeliveryStatus.ACCEPT, 3000, LocalDateTime.now());
        Delivery deliveryInstruct = Delivery.of(DeliveryStatus.PREPARING, 3000, LocalDateTime.now());
        Delivery deliveryDelivering = Delivery.of(DeliveryStatus.DELIVERING, 3000, LocalDateTime.now());
        Delivery deliveryFinalDelivery = Delivery.of(DeliveryStatus.FINAL_DELIVERY, 3000, LocalDateTime.now());

        deliveryDelivering.completeDelivery();

        assertThatThrownBy(deliveryAccept::completeDelivery).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(deliveryInstruct::completeDelivery).isInstanceOf(IllegalArgumentException.class);
        assertThat(deliveryDelivering.getDeliveryStatus()).isEqualTo(DeliveryStatus.FINAL_DELIVERY);
        assertThatThrownBy(deliveryFinalDelivery::completeDelivery).isInstanceOf(IllegalArgumentException.class);
    }
}
