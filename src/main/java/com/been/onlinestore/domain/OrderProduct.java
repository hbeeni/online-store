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
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
public class OrderProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ToString.Exclude
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	private Order order;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int quantity;

	protected OrderProduct() {
	}

	private OrderProduct(Product product, int price, int quantity) {
		this.product = product;
		this.price = price;
		this.quantity = quantity;
	}

	public static OrderProduct create(Product product, int quantity) {
		product.removeStock(quantity);
		return new OrderProduct(product, product.getPrice(), quantity);
	}

	public int getTotalPrice() {
		return price * quantity;
	}

	public void addStock() {
		product.addStock(quantity);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OrderProduct that)) {
			return false;
		}
		return this.getId() != null && Objects.equals(this.getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}
}
