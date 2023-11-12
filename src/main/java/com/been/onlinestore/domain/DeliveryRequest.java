package com.been.onlinestore.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Entity
public class DeliveryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 50)
    private String deliveryAddress;

    @Column(nullable = false, length = 20)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    protected DeliveryRequest() {}

    private DeliveryRequest(String deliveryAddress, String receiverName, String receiverPhone) {
        this.deliveryAddress = deliveryAddress;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
    }

    public static DeliveryRequest of(String deliveryAddress, String receiverName, String receiverPhone) {
        return new DeliveryRequest(deliveryAddress, receiverName, receiverPhone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryRequest that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
