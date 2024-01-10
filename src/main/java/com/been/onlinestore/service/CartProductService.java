package com.been.onlinestore.service;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.CartProductRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.request.CartProductServiceRequest;
import com.been.onlinestore.service.dto.response.CartProductResponse;
import com.been.onlinestore.service.dto.response.CartResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CartProductService {

	private final CartProductRepository cartProductRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderService orderService;

	@Transactional(readOnly = true)
	public CartResponse findCartProducts(Long userId) {
		List<CartProduct> cartProducts = cartProductRepository.findAllOnSaleOrOutOfStockByUserId(userId);
		int deliveryFee = getDeliveryFee(cartProducts);
		return CartResponse.from(cartProducts, deliveryFee);
	}

	public CartProductResponse addCartProduct(Long userId, CartProductServiceRequest.Create serviceRequest) {
		Product product = productRepository.findOnSaleById(serviceRequest.productId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage()));

		Optional<CartProduct> cartProductOptional =
			cartProductRepository.findByUserIdAndProductId(userId, serviceRequest.productId());

		CartProduct cartProduct;

		if (cartProductOptional.isPresent()) {
			cartProduct = cartProductOptional.get();
			cartProduct.addQuantity(serviceRequest.productQuantity());
		} else {
			User user = userRepository.getReferenceById(userId);
			cartProduct = cartProductRepository.save(serviceRequest.toEntity(user, product));
		}

		return CartProductResponse.from(cartProduct);
	}

	public Long order(Long userId, CartProductServiceRequest.Order serviceRequest) {
		List<Long> cartProductIds = serviceRequest.cartProductIds();

		Long orderId = orderService.order(userId, serviceRequest, createOrderProductMap(userId, cartProductIds));

		//장바구니에서 주문한 장바구니 상품 삭제
		deleteCartProducts(userId, cartProductIds);

		return orderId;
	}

	public CartProductResponse updateCartProductQuantity(Long userId, Long cartProductId, int updateProductQuantity) {
		CartProduct cartProduct = cartProductRepository.findCartProduct(userId, cartProductId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_CART_PRODUCT.getMessage()));
		cartProduct.updateQuantity(updateProductQuantity);
		return CartProductResponse.from(cartProduct);
	}

	public void deleteCartProducts(Long userId, List<Long> cartProductIds) {
		cartProductRepository.deleteCartProducts(userId, cartProductIds);
	}

	/**
	 * 매일 자정에 30일이 지난 장바구니 상품 삭제
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void cleanUpExpiredCartProducts() {
		log.info("CartProductService.cleanUpExpiredCartProducts 실행");

		LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
		List<CartProduct> expiredCartProducts = cartProductRepository.findAllByModifiedAtBefore(thirtyDaysAgo);
		List<Long> expiredCartProductIds = expiredCartProducts.stream()
			.map(CartProduct::getId)
			.toList();

		cartProductRepository.deleteAllByIdInBatch(expiredCartProductIds);
	}

	private Map<Long, Integer> createOrderProductMap(Long userId, List<Long> cartProductIds) {
		List<CartProduct> cartProducts = cartProductRepository.findAllOnSaleByIdInAndUserId(userId, cartProductIds);
		if (cartProducts.size() != cartProductIds.size()) {
			throw new EntityNotFoundException(ErrorMessages.CANNOT_ORDER_CART_PRODUCT_INCLUDED.getMessage());
		}

		return cartProducts.stream()
			.collect(toMap(
				cartProduct -> cartProduct.getProduct().getId(),
				CartProduct::getQuantity
			));
	}

	private static int getDeliveryFee(List<CartProduct> cartProducts) {
		return cartProducts.stream()
			.map(CartProduct::getProduct)
			.filter(Product::isOnSale)
			.map(Product::getDeliveryFee)
			.min(Comparator.naturalOrder())
			.orElse(0);
	}
}
