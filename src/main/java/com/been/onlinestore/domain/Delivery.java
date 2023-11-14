package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.DeliveryStatus;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.LocalDateTime.now;

@Getter
@ToString(callSuper = true)
@Entity
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACCEPT'")
    @Column(nullable = false, length = 20)
    private DeliveryStatus deliveryStatus;

    @Column(nullable = false)
    private int deliveryFee;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deliveredAt;

    protected Delivery() {}

    private Delivery(DeliveryStatus deliveryStatus, int deliveryFee, LocalDateTime deliveredAt) {
        this.deliveryStatus = deliveryStatus;
        this.deliveryFee = deliveryFee;
        this.deliveredAt = deliveredAt;
    }

    public static Delivery of(DeliveryStatus deliveryStatus, int deliveryFee, LocalDateTime deliveredAt) {
        return new Delivery(deliveryStatus, deliveryFee, deliveredAt);
    }

    public void startPreparing() {
        if (this.deliveryStatus != DeliveryStatus.ACCEPT) {
            throw new IllegalArgumentException("상품 준비 단계로 넘어갈 수 없습니다. 현재 배송 상태 = " + this.deliveryStatus.getDescription());
        }
        this.deliveryStatus = DeliveryStatus.PREPARING;
    }

    public void startDelivery() {
        if (this.deliveryStatus != DeliveryStatus.PREPARING) {
            throw new IllegalArgumentException("배송을 시작할 수 없습니다. 현재 배송 상태 = " + this.deliveryStatus.getDescription());
        }
        this.deliveryStatus = DeliveryStatus.DELIVERING;
    }

    public void completeDelivery() {
        if (this.deliveryStatus != DeliveryStatus.DELIVERING) {
            throw new IllegalArgumentException("배송 중이 아닙니다. 현재 배송 상태 = " + this.deliveryStatus.getDescription());
        }
        this.deliveryStatus = DeliveryStatus.FINAL_DELIVERY;
        this.deliveredAt = now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Delivery that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
