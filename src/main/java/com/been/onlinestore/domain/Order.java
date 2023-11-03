package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.OrderStatus;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(name = "orders")
@Entity
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private User orderer;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    private Delivery delivery;


    @Column(nullable = false, length = 20)
    private String ordererPhone;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ORDER'")
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    protected Order() {}

    private Order(User orderer, Delivery delivery, String ordererPhone, OrderStatus orderStatus) {
        this.orderer = orderer;
        this.delivery = delivery;
        this.ordererPhone = ordererPhone;
        this.orderStatus = orderStatus;
    }

    public static Order of(User user, Delivery delivery, String ordererPhone, OrderStatus orderStatus) {
        return new Order(user, delivery, ordererPhone, orderStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
