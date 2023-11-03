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

    @Column(nullable = false, length = 50)
    private String deliveryAddress;

    @Column(nullable = false)
    private int deliveryFee;

    @Column(nullable = false, length = 20)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deliveredAt;

    protected Delivery() {}

    private Delivery(DeliveryStatus deliveryStatus, String deliveryAddress, int deliveryFee, String receiverName, String receiverPhone, LocalDateTime deliveredAt) {
        this.deliveryStatus = deliveryStatus;
        this.deliveryAddress = deliveryAddress;
        this.deliveryFee = deliveryFee;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.deliveredAt = deliveredAt;
    }

    public static Delivery of(DeliveryStatus deliveryStatus, String deliveryAddress, int deliveryFee, String receiverName, String receiverPhone, LocalDateTime deliveredAt) {
        return new Delivery(deliveryStatus, deliveryAddress, deliveryFee, receiverName, receiverPhone, deliveredAt);
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
