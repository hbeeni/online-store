package com.been.onlinestore.repository.querydsl.product;

import static com.been.onlinestore.domain.QCategory.*;
import static com.been.onlinestore.domain.QProduct.*;
import static com.been.onlinestore.domain.QUser.*;
import static org.springframework.util.StringUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.file.ImageStore;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

public class ProductRepositoryCustomImpl extends QuerydslRepositorySupport implements ProductRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final ImageStore imageStore;

	public ProductRepositoryCustomImpl(EntityManager em, ImageStore imageStore) {
		super(Product.class);
		this.queryFactory = new JPAQueryFactory(em);
		this.imageStore = imageStore;
	}

	@Override
	public Page<AdminProductResponse> searchProducts(Long sellerId, ProductSearchCondition cond, Pageable pageable) {
		List<AdminProductResponse> content = queryFactory
				.select(getAdminProductResponseProjection())
				.from(product)
				.leftJoin(product.category, category)
				.join(product.seller, user)
				.where(sellerIdEq(sellerId),
						categoryIdEq(cond.categoryId()),
						productNameContains(cond.name()),
						saleStatusEq(cond.saleStatus())
				)
				.orderBy(getOrderSpecifiers(pageable))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		JPAQuery<Long> countQuery = queryFactory
				.select(product.count())
				.from(product)
				.leftJoin(product.category, category)
				.join(product.seller, user)
				.where(sellerIdEq(sellerId),
						categoryIdEq(cond.categoryId()),
						productNameContains(cond.name()),
						saleStatusEq(cond.saleStatus())
				);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	@Override
	public Optional<AdminProductResponse> searchProduct(Long productId, Long sellerId) {
		AdminProductResponse adminProductResponse = queryFactory
				.select(getAdminProductResponseProjection())
				.from(product)
				.leftJoin(product.category, category)
				.join(product.seller, user)
				.where(productIdEq(productId),
						sellerIdEq(sellerId)
				)
				.fetchOne();
		return Optional.ofNullable(adminProductResponse);
	}

	private ConstructorExpression<AdminProductResponse> getAdminProductResponseProjection() {
		return Projections.constructor(AdminProductResponse.class,
				product.id,
				category.name,
				Projections.constructor(AdminProductResponse.Seller.class,
						user.id,
						user.uid
				),
				product.name,
				product.price,
				product.description,
				product.stockQuantity,
				product.salesVolume,
				product.saleStatus,
				product.deliveryFee,
				product.imageName.prepend(imageStore.getImagePath()),
				product.createdAt,
				product.createdBy,
				product.modifiedAt,
				product.modifiedBy
		);
	}

	private BooleanExpression productIdEq(Long productId) {
		return productId != null ? product.id.eq(productId) : null;
	}

	private BooleanExpression sellerIdEq(Long sellerId) {
		return sellerId != null ? user.id.eq(sellerId) : null;
	}

	private BooleanExpression categoryIdEq(Long categoryId) {
		return categoryId != null ? category.id.eq(categoryId) : null;
	}

	private BooleanExpression productNameContains(String productName) {
		return hasText(productName) ? product.name.contains(productName) : null;
	}

	private BooleanExpression saleStatusEq(SaleStatus saleStatus) {
		return saleStatus != null ? product.saleStatus.eq(saleStatus) : null;
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
			Order direction = order.isAscending() ? Order.ASC : Order.DESC;
			String property = order.getProperty();
			PathBuilder<Product> pathBuilder = new PathBuilder<>(Product.class, "product");
			orderSpecifiers.add(new OrderSpecifier(direction, pathBuilder.get(property)));
		});

		return orderSpecifiers;
	}
}
