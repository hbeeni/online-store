package com.been.onlinestore.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Getter
@ToString
@Entity
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;


    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    protected OrderProduct() {}

    private OrderProduct(Order order, Product product, int price, int quantity) {
        this.order = order;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderProduct of(Order order, Product product, int price, int quantity) {
        return new OrderProduct(order, product, price, quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderProduct that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
