package com.been.onlinestore.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private DeliveryRequest deliveryRequest;

	@ToString.Exclude
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	private Delivery delivery;

	@ToString.Exclude
	@Setter
	@OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
	private List<OrderProduct> orderProducts = new ArrayList<>();

	@Column(nullable = false, length = 20)
	private String ordererPhone;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'ORDER'")
	@Column(nullable = false, length = 20)
	private OrderStatus orderStatus;

	protected Order() {
	}

	private Order(
		User orderer, DeliveryRequest deliveryRequest, Delivery delivery, String ordererPhone, OrderStatus orderStatus
	) {
		this.orderer = orderer;
		this.deliveryRequest = deliveryRequest;
		this.delivery = delivery;
		this.ordererPhone = ordererPhone;
		this.orderStatus = orderStatus;
	}

	public static Order create(
		User orderer, DeliveryRequest deliveryRequest, String ordererPhone, OrderStatus orderStatus, int deliveryFee
	) {
		Delivery delivery = Delivery.of(DeliveryStatus.ACCEPT, deliveryFee, null);
		return new Order(orderer, deliveryRequest, delivery, ordererPhone, orderStatus);
	}

	public void addOrderProducts(List<OrderProduct> orderProducts) {
		this.orderProducts = orderProducts;
		orderProducts.forEach(orderProduct -> orderProduct.setOrder(this));
	}

	public int getTotalPrice() {
		return orderProducts.stream()
			.mapToInt(OrderProduct::getTotalPrice)
			.sum();
	}

	public void cancel() {
		if (this.delivery.getDeliveryStatus() != DeliveryStatus.ACCEPT) {
			throw new IllegalStateException(ErrorMessages.CANNOT_CANCEL_ORDER_PRODUCT.getMessage());
		}
		orderProducts.forEach(OrderProduct::addStock);
		this.orderStatus = OrderStatus.CANCEL;
	}

	public void startPreparing() {
		this.getDelivery().startPreparing();
	}

	public void startDelivery() {
		this.getDelivery().startDelivery();
	}

	public void completeDelivery() {
		this.getDelivery().completeDelivery();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Order that)) {
			return false;
		}
		return this.getId() != null && Objects.equals(this.getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}
}
