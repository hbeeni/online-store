package com.been.onlinestore.domain;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.constant.DeliveryStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import java.util.Objects;

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

	@ToString.Exclude
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Delivery delivery;

	@Column(nullable = false)
	private int price;

	@Column(nullable = false)
	private int quantity;

	protected OrderProduct() {
	}

	private OrderProduct(Order order, Product product, Delivery delivery, int price, int quantity) {
		this.order = order;
		this.product = product;
		this.delivery = delivery;
		this.price = price;
		this.quantity = quantity;
	}

	/**
	 * 자동으로 배송 정보를 생성하고, 상품의 재고를 감소시킨다.
	 */
	public static OrderProduct of(Product product, int quantity) {
		product.removeStock(quantity);
		Delivery delivery = Delivery.of(DeliveryStatus.ACCEPT, product.getDeliveryFee(), null);
		return new OrderProduct(null, product, delivery, product.getPrice(), quantity);
	}

	public int getTotalPrice() {
		return price * quantity;
	}

	public void cancel() {
		if (this.delivery.getDeliveryStatus() != DeliveryStatus.ACCEPT) {
			throw new IllegalStateException(ErrorMessages.CANNOT_CANCEL_ORDER_PRODUCT.getMessage());
		}
		product.addStock(this.quantity);
	}

	public boolean canStartPreparing() {
		return this.getDelivery().canStartPreparing();
	}

	public boolean canStartDelivery() {
		return this.getDelivery().canStartDelivery();
	}

	public boolean canCompleteDelivery() {
		return this.getDelivery().canCompleteDelivery();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof OrderProduct that))
			return false;
		return this.getId() != null && Objects.equals(this.getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}
}
