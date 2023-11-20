package com.been.onlinestore.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
@Entity
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ToString.Exclude
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProduct> cartProducts = new ArrayList<>();

    protected Cart() {}

    private Cart(User user) {
        this.user = user;
    }

    public static Cart of(User user) {
        return new Cart(user);
    }

    public void addCartProduct(CartProduct cartProduct) {
        this.cartProducts.add(cartProduct);
        cartProduct.setCart(this);
    }

    public int getTotalPrice() {
        return cartProducts.stream()
                .mapToInt(CartProduct::getTotalPrice)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
