package com.been.onlinestore.repository.querydsl.order;

import static com.been.onlinestore.domain.QDelivery.*;
import static com.been.onlinestore.domain.QDeliveryRequest.*;
import static com.been.onlinestore.domain.QOrder.*;
import static com.been.onlinestore.domain.QOrderProduct.*;
import static com.been.onlinestore.domain.QProduct.*;
import static com.been.onlinestore.domain.QUser.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
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
		List<Order> orders = findOrders(ordererId, pageable);

		JPAQuery<Long> countQuery = queryFactory
			.select(order.count())
			.from(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.where(user.id.eq(ordererId));

		return PageableExecutionUtils.getPage(orders, pageable, countQuery::fetchOne);
	}

	@Override
	public Optional<Order> findOrderByOrderer(Long orderId, Long ordererId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(order)
				.join(order.orderer, user).fetchJoin()
				.join(order.deliveryRequest, deliveryRequest).fetchJoin()
				.join(order.delivery, delivery).fetchJoin()
				.join(order.orderProducts, orderProduct).fetchJoin()
				.join(orderProduct.product, product).fetchJoin()
				.where(order.id.eq(orderId),
					order.orderer.id.eq(ordererId))
				.fetchOne()
		);
	}

	public Page<Order> findOrdersForAdmin(OrderSearchCondition cond, Pageable pageable) {
		List<Order> orders = findOrders(cond, pageable);

		JPAQuery<Long> countQuery = queryFactory
			.select(order.countDistinct())
			.from(order)
			.join(order.orderer, user)
			.join(order.deliveryRequest, deliveryRequest)
			.join(order.orderProducts, orderProduct)
			.join(orderProduct.product, product)
			.where(
				ordererIdEq(cond.ordererId()),
				productIdEq(cond.productId()),
				deliveryStatusEq(cond.deliveryStatus()),
				orderStatusEq(cond.orderStatus())
			);

		return PageableExecutionUtils.getPage(orders, pageable, countQuery::fetchOne);
	}

	public Optional<Order> findOrderByIdForAdmin(Long orderId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(order)
				.join(order.orderer, user).fetchJoin()
				.join(order.deliveryRequest, deliveryRequest).fetchJoin()
				.join(order.orderProducts, orderProduct).fetchJoin()
				.join(order.delivery, delivery).fetchJoin()
				.join(orderProduct.product, product).fetchJoin()
				.where(order.id.eq(orderId))
				.fetchOne()
		);
	}

	private List<Order> findOrders(Long ordererId, Pageable pageable) {
		List<Order> orders = queryFactory
			.selectFrom(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.delivery, delivery).fetchJoin()
			.where(user.id.eq(ordererId))
			.orderBy(getOrderSpecifiers(pageable))
			.fetch();

		setOrderProducts(orders);

		return orders;
	}

	private List<Order> findOrders(OrderSearchCondition cond, Pageable pageable) {
		List<Order> orders = queryFactory
			.selectDistinct(order)
			.from(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.delivery, delivery).fetchJoin()
			.join(order.orderProducts, orderProduct)
			.join(orderProduct.product, product)
			.where(
				ordererIdEq(cond.ordererId()),
				productIdEq(cond.productId()),
				deliveryStatusEq(cond.deliveryStatus()),
				orderStatusEq(cond.orderStatus())
			)
			.orderBy(getOrderSpecifiers(pageable))
			.fetch();

		setOrderProducts(orders);

		return orders;
	}

	private void setOrderProducts(List<Order> orders) {
		Map<Long, List<OrderProduct>> orderProductMap = findOrderIdToOrderProductMap(toOrderIds(orders));
		orders.forEach(o -> o.setOrderProducts(orderProductMap.get(o.getId())));
	}

	private List<Long> toOrderIds(List<Order> orders) {
		return orders.stream()
			.map(Order::getId)
			.toList();
	}

	private Map<Long, List<OrderProduct>> findOrderIdToOrderProductMap(List<Long> orderIds) {
		List<OrderProduct> orderProducts = queryFactory
			.selectFrom(orderProduct)
			.join(orderProduct.product, product).fetchJoin()
			.where(orderProduct.order.id.in(orderIds))
			.fetch();

		return orderProducts.stream()
			.collect(Collectors.groupingBy(orderProduct -> orderProduct.getOrder().getId()));
	}

	private BooleanExpression ordererIdEq(Long ordererId) {
		return ordererId != null ? user.id.eq(ordererId) : null;
	}

	private BooleanExpression productIdEq(Long productId) {
		return productId != null ? product.id.eq(productId) : null;
	}

	private BooleanExpression deliveryStatusEq(DeliveryStatus deliveryStatus) {
		return deliveryStatus != null ? delivery.deliveryStatus.eq(deliveryStatus) : null;
	}

	private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
		return orderStatus != null ? order.orderStatus.eq(orderStatus) : null;
	}

	@SuppressWarnings("rawtypes")
	private OrderSpecifier[] getOrderSpecifiers(Pageable pageable) {
		List<OrderSpecifier> orderSpecifiers = getOrderSpecifiers(pageable.getSort());
		return orderSpecifiers.toArray(OrderSpecifier[]::new);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private List<OrderSpecifier> getOrderSpecifiers(Sort sort) {
		List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
		sort.stream().forEach(order -> {
			com.querydsl.core.types.Order direction =
				order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;
			String property = order.getProperty();
			PathBuilder<Order> pathBuilder = new PathBuilder<>(Order.class, "order1");
			orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(property)));
		});

		return orderSpecifiers;
	}
}
