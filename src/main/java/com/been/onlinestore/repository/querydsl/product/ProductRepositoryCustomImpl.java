package com.been.onlinestore.repository.querydsl.product;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.dto.ProductSearchCondition;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static com.been.onlinestore.domain.QCategory.category;
import static com.been.onlinestore.domain.QProduct.product;
import static com.been.onlinestore.domain.QUser.user;
import static org.springframework.util.StringUtils.hasText;

public class ProductRepositoryCustomImpl extends QuerydslRepositorySupport implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryCustomImpl(EntityManager em) {
        super(Product.class);
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Product> searchProducts(ProductSearchCondition cond, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category)
                .leftJoin(product.seller, user)
                .where(categoryIdEq(cond.categoryId()),
                        productNameContains(cond.name()),
                        saleStatusEq(cond.saleStatus()))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.seller, user)
                .where(categoryIdEq(cond.categoryId()),
                        productNameContains(cond.name()),
                        saleStatusEq(cond.saleStatus()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Product> searchProductsBySellerId(Long sellerId, ProductSearchCondition cond, Pageable pageable) {
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category)
                .leftJoin(product.seller, user)
                .where(user.id.eq(sellerId),
                        categoryIdEq(cond.categoryId()),
                        productNameContains(cond.name()),
                        saleStatusEq(cond.saleStatus()))
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .leftJoin(product.category, category)
                .leftJoin(product.seller, user)
                .where(user.id.eq(sellerId),
                        categoryIdEq(cond.categoryId()),
                        productNameContains(cond.name()),
                        saleStatusEq(cond.saleStatus()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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
                }
        );

        return orderSpecifiers;
    }
}
