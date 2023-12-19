package com.been.onlinestore.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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

@RequiredArgsConstructor
@Transactional
@Service
public class CartProductService {

	private final CartProductRepository cartProductRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public CartResponse findCartProducts(Long userId) {
		List<CartProduct> cartProducts = cartProductRepository.findAllByUserId(userId);
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

	public CartProductResponse updateCartProductQuantity(Long userId, Long cartProductId, int updateProductQuantity) {
		CartProduct cartProduct = cartProductRepository.findCartProduct(userId, cartProductId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_CART_PRODUCT.getMessage()));
		cartProduct.updateQuantity(updateProductQuantity);
		return CartProductResponse.from(cartProduct);
	}

	public void deleteCartProducts(Long userId, List<Long> cartProductIds) {
		cartProductRepository.deleteCartProducts(userId, cartProductIds);
	}

	private static int getDeliveryFee(List<CartProduct> cartProducts) {
		return cartProducts.stream()
			.map(CartProduct::getProduct)
			.map(Product::getDeliveryFee)
			.min(Comparator.naturalOrder())
			.orElse(0);
	}
}
