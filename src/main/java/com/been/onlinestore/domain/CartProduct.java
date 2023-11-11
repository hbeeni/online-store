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
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;


    @Column(nullable = false)
    private int productQuantity;

    protected CartProduct() {}

    private CartProduct(Cart cart, Product product, int productQuantity) {
        this.cart = cart;
        this.product = product;
        this.productQuantity = productQuantity;
    }

    public static CartProduct of(Cart cart, Product product, int productQuantity) {
        return new CartProduct(cart, product, productQuantity);
    }

    public void updateProductQuantity(int productQuantity) {
        if (productQuantity > 0) {
            this.productQuantity = productQuantity;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartProduct that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
