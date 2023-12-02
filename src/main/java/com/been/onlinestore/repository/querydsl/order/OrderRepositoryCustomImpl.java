package com.been.onlinestore.repository.querydsl.order;

import static com.been.onlinestore.domain.QDelivery.*;
import static com.been.onlinestore.domain.QDeliveryRequest.*;
import static com.been.onlinestore.domain.QOrder.*;
import static com.been.onlinestore.domain.QOrderProduct.*;
import static com.been.onlinestore.domain.QProduct.*;
import static com.been.onlinestore.domain.QUser.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class OrderRepositoryCustomImpl extends QuerydslRepositorySupport implements OrderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public OrderRepositoryCustomImpl(EntityManager em) {
		super(Order.class);
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Order> findAllOrdersByOrderer(Long ordererId, Pageable pageable) {
		List<Order> result = findOrdersByOrderer(ordererId);

		Map<Long, List<OrderProduct>> orderProductMap = findOrderProductMapByOrderIds(toOrderIds(result));
		result.forEach(o -> o.setOrderProducts(orderProductMap.get(o.getId())));

		JPAQuery<Long> countQuery = queryFactory.select(order.count())
			.from(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.where(user.id.eq(ordererId));

		return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
	}

	@Override
	public Optional<Order> findOrderByOrderer(Long orderId, Long ordererId) {
		Order result = queryFactory
			.selectFrom(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.orderProducts, orderProduct).fetchJoin()
			.join(orderProduct.product, product)
			.join(orderProduct.delivery, delivery).fetchJoin()
			.where(order.id.eq(orderId),
				order.orderer.id.eq(ordererId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	@Override
	public Page<Order> findAllOrdersBySeller(Long sellerId, Pageable pageable) {
		List<Order> result = findOrdersBySeller(sellerId);

		Map<Long, List<OrderProduct>> orderProductMap = findOrderProductMapBySellerId(sellerId);
		result.forEach(o -> o.setOrderProducts(orderProductMap.get(o.getId())));

		JPAQuery<Long> countQuery = queryFactory
			.selectDistinct(order.count())
			.from(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.orderProducts, orderProduct)
			.join(orderProduct.product, product)
			.where(product.seller.id.eq(sellerId));

		return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
	}

	@Override
	public Optional<Order> findOrderBySeller(Long orderId, Long sellerId) {
		Order result = queryFactory
			.selectFrom(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.orderProducts, orderProduct).fetchJoin()
			.join(orderProduct.product, product).fetchJoin()
			.join(orderProduct.delivery, delivery).fetchJoin()
			.where(order.id.eq(orderId),
				product.seller.id.eq(sellerId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	private List<Long> toOrderIds(List<Order> orders) {
		return orders.stream()
			.map(Order::getId)
			.toList();
	}

	private List<Order> findOrdersByOrderer(Long ordererId) {
		return queryFactory
			.selectFrom(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.where(user.id.eq(ordererId))
			.fetch();
	}

	private List<Order> findOrdersBySeller(Long sellerId) {
		return queryFactory
			.selectDistinct(order)
			.from(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.orderProducts, orderProduct)
			.join(orderProduct.product, product)
			.where(product.seller.id.eq(sellerId))
			.fetch();
	}

	private Map<Long, List<OrderProduct>> findOrderProductMapByOrderIds(List<Long> orderIds) {
		List<OrderProduct> orderProducts = queryFactory
			.selectFrom(orderProduct)
			.join(orderProduct.product, product).fetchJoin()
			.join(orderProduct.delivery, delivery).fetchJoin()
			.where(orderProduct.order.id.in(orderIds))
			.fetch();

		return orderProducts.stream()
			.collect(Collectors.groupingBy(orderProduct -> orderProduct.getOrder().getId()));
	}

	private Map<Long, List<OrderProduct>> findOrderProductMapBySellerId(Long sellerId) {
		List<OrderProduct> orderProducts = queryFactory
			.selectFrom(orderProduct)
			.join(orderProduct.product, product).fetchJoin()
			.join(orderProduct.delivery, delivery).fetchJoin()
			.where(product.seller.id.eq(sellerId))
			.fetch();

		return orderProducts.stream()
			.collect(Collectors.groupingBy(orderProduct1 -> orderProduct1.getOrder().getId()));
	}
}
