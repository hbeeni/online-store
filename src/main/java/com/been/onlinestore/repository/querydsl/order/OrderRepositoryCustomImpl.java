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
import com.been.onlinestore.domain.QUser;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class OrderRepositoryCustomImpl extends QuerydslRepositorySupport implements OrderRepositoryCustom {

	private static final QUser orderer = new QUser("orderer");

	private final JPAQueryFactory queryFactory;

	public OrderRepositoryCustomImpl(EntityManager em) {
		super(Order.class);
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Order> findAllOrdersByOrderer(Long ordererId, Pageable pageable) {
		List<Order> result = findOrdersByOrderer(ordererId, pageable);

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
			.join(order.delivery, delivery).fetchJoin()
			.join(orderProduct.product, product)
			.where(order.id.eq(orderId),
				order.orderer.id.eq(ordererId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	public Page<Order> searchOrders(OrderSearchCondition cond, Pageable pageable) {
		List<Order> result = findOrdersWithCond(cond);

		Map<Long, List<OrderProduct>> orderProductMap = findOrderProductMapByCond(
			cond.productId(), cond.deliveryStatus()
		);
		result.forEach(o -> o.setOrderProducts(orderProductMap.get(o.getId())));

		JPAQuery<Long> countQuery = queryFactory
			.selectDistinct(order.count())
			.from(order)
			.join(order.orderer, orderer)
			.join(order.deliveryRequest, deliveryRequest)
			.join(order.orderProducts, orderProduct)
			.join(orderProduct.product, product)
			.where(
				ordererIdEq(cond.ordererId()),
				productIdEq(cond.productId()),
				deliveryStatusEq(cond.deliveryStatus()),
				orderStatusEq(cond.orderStatus())
			);

		return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
	}

	public Optional<Order> findOrderByIdForAdmin(Long orderId) {
		Order result = queryFactory
			.selectFrom(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.orderProducts, orderProduct).fetchJoin()
			.join(order.delivery, delivery).fetchJoin()
			.join(orderProduct.product, product).fetchJoin()
			.where(order.id.eq(orderId))
			.fetchOne();
		return Optional.ofNullable(result);
	}

	private List<Long> toOrderIds(List<Order> orders) {
		return orders.stream()
			.map(Order::getId)
			.toList();
	}

	private List<Order> findOrdersByOrderer(Long ordererId, Pageable pageable) {
		return queryFactory
			.selectFrom(order)
			.join(order.orderer, user).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.where(user.id.eq(ordererId))
			.orderBy(getOrderSpecifiers(pageable))
			.fetch();
	}

	private Map<Long, List<OrderProduct>> findOrderProductMapByOrderIds(List<Long> orderIds) {
		List<OrderProduct> orderProducts = queryFactory
			.selectFrom(orderProduct)
			.join(orderProduct.product, product).fetchJoin()
			.where(orderProduct.order.id.in(orderIds))
			.fetch();

		return orderProducts.stream()
			.collect(Collectors.groupingBy(orderProduct -> orderProduct.getOrder().getId()));
	}

	private List<Order> findOrdersWithCond(OrderSearchCondition cond) {
		return queryFactory
			.selectDistinct(order)
			.from(order)
			.join(order.orderer, orderer).fetchJoin()
			.join(order.deliveryRequest, deliveryRequest).fetchJoin()
			.join(order.orderProducts, orderProduct)
			.join(order.delivery, delivery)
			.join(orderProduct.product, product)
			.where(
				ordererIdEq(cond.ordererId()),
				productIdEq(cond.productId()),
				deliveryStatusEq(cond.deliveryStatus()),
				orderStatusEq(cond.orderStatus())
			)
			.fetch();
	}

	private Map<Long, List<OrderProduct>> findOrderProductMapByCond(
		Long productId, DeliveryStatus deliveryStatus
	) {
		List<OrderProduct> orderProducts = queryFactory
			.selectFrom(orderProduct)
			.join(orderProduct.product, product).fetchJoin()
			.where(
				productIdEq(productId),
				deliveryStatusEq(deliveryStatus)
			)
			.fetch();

		return orderProducts.stream()
			.collect(Collectors.groupingBy(op -> op.getOrder().getId()));
	}

	private BooleanExpression ordererIdEq(Long ordererId) {
		return ordererId != null ? orderer.id.eq(ordererId) : null;
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
