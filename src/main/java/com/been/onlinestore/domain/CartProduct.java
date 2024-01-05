package com.been.onlinestore.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Entity
public class CartProduct extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

	@Column(nullable = false)
	private int quantity;

	protected CartProduct() {
	}

	private CartProduct(User user, Product product, int quantity) {
		this.user = user;
		this.product = product;
		this.quantity = quantity;
	}

	public static CartProduct of(User user, Product product, int quantity) {
		return new CartProduct(user, product, quantity);
	}

	public void addQuantity(int quantity) {
		if (quantity > 0) {
			this.quantity += quantity;
		}
	}

	public void updateQuantity(int quantity) {
		if (quantity > 0) {
			this.quantity = quantity;
		}
	}

	public int getTotalPrice() {
		return product.getPrice() * quantity;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CartProduct that))
			return false;
		return this.getId() != null && Objects.equals(this.getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}
}
