package com.been.onlinestore.service;

import static com.been.onlinestore.util.ProductTestDataUtil.*;
import static com.been.onlinestore.util.UserTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.repository.CartProductRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.request.CartProductServiceRequest;
import com.been.onlinestore.service.dto.response.CartProductResponse;
import com.been.onlinestore.service.dto.response.CartResponse;

@DisplayName("비즈니스 로직 - 장바구니 상품")
@ExtendWith(MockitoExtension.class)
class CartProductServiceTest {

	private static final Long userId = TestSecurityConfig.USER_ID;

	@Mock
	private CartProductRepository cartProductRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ProductRepository productRepository;
	@Mock
	private OrderService orderService;

	@InjectMocks
	private CartProductService sut;

	@DisplayName("장바구니에 있는 모든 상품을 조회한다.")
	@Test
	void test_findCartProducts() {
		//Given
		Long productId = 1L;
		int quantity = 1;

		given(cartProductRepository.findAllByUserId(userId))
			.willReturn(List.of(createCartProduct(productId, quantity)));

		//When
		CartResponse result = sut.findCartProducts(userId);

		//Then
		assertThat(result).isNotNull();
		then(cartProductRepository).should().findAllByUserId(userId);
	}

	@DisplayName("장바구니에 상품을 추가한다.")
	@Test
	void test_addCartProduct() {
		//Given
		Long productId = 1L;
		int quantity = 1;
		CartProductServiceRequest.Create serviceRequest = CartProductServiceRequest.Create.of(productId, quantity);
		CartProduct savedCartProduct = createSavedCartProduct(productId, quantity);

		given(productRepository.findOnSaleById(productId)).willReturn(Optional.of(createProduct(productId)));
		given(cartProductRepository.findByUserIdAndProductId(userId, productId)).willReturn(Optional.empty());
		given(userRepository.getReferenceById(userId)).willReturn(createUser(userId));
		given(cartProductRepository.save(any())).willReturn(savedCartProduct);

		//When
		CartProductResponse result = sut.addCartProduct(userId, serviceRequest);

		//Then
		assertThat(result.quantity()).isEqualTo(quantity);
		then(productRepository).should().findOnSaleById(productId);
		then(cartProductRepository).should().findByUserIdAndProductId(userId, productId);
		then(userRepository).should().getReferenceById(userId);
		then(cartProductRepository).should().save(any());
	}

	@DisplayName("장바구니에 상품을 추가할 때, 해당 상품이 이미 장바구니에 존재하면 수량을 추가한다.")
	@Test
	void test_addCartProduct_whenCartProductIsAlreadyExisting() {
		//Given
		Long productId = 1L;
		int originalQuantity = 1;
		int addQuantity = 2;
		CartProductServiceRequest.Create serviceRequest = CartProductServiceRequest.Create.of(productId, addQuantity);

		given(productRepository.findOnSaleById(productId)).willReturn(Optional.of(createProduct(productId)));
		given(cartProductRepository.findByUserIdAndProductId(userId, productId))
			.willReturn(Optional.of(createCartProduct(productId, originalQuantity)));

		//When
		CartProductResponse result = sut.addCartProduct(userId, serviceRequest);

		//Then
		assertThat(result.quantity()).isEqualTo(originalQuantity + addQuantity);
		then(productRepository).should().findOnSaleById(productId);
		then(cartProductRepository).should().findByUserIdAndProductId(userId, productId);
	}

	@DisplayName("장바구니에 상품을 추가할 때, 해당 상품이 존재하지 않으면 예외가 발생한다.")
	@Test
	void test_addCartProduct_throwsEntityNotFoundException() {
		//Given
		Long productId = 1L;
		int addQuantity = 2;
		CartProductServiceRequest.Create serviceRequest = CartProductServiceRequest.Create.of(productId, addQuantity);

		given(productRepository.findOnSaleById(productId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.addCartProduct(userId, serviceRequest))
			.isInstanceOf(EntityNotFoundException.class);
		then(productRepository).should().findOnSaleById(productId);
		then(cartProductRepository).shouldHaveNoInteractions();
		then(userRepository).shouldHaveNoInteractions();
	}

	@DisplayName("장바구니에서 상품을 주문하면, 주문 상품과 배송 정보를 함께 저장하고 장바구니에서 주문한 상품을 삭제한 후, 저장된 주문의 id를 반환한다.")
	@Test
	void test_order() {
		//Given
		long orderId = 1L;
		long productId1 = 1L;
		int quantity1 = 1;
		long productId2 = 2L;
		int quantity2 = 2;

		List<Long> cartProductIds = List.of(1L, 2L);
		CartProductServiceRequest.Order serviceRequest =
			new CartProductServiceRequest.Order(cartProductIds, "address", "name", "01011112222");
		Map<Long, Integer> orderProductMap = Map.of(productId1, quantity1, productId2, quantity2);

		given(cartProductRepository.findCartProducts(userId, cartProductIds))
			.willReturn(List.of(createCartProduct(productId1, quantity1), createCartProduct(productId2, quantity2)));
		given(orderService.order(userId, serviceRequest, orderProductMap)).willReturn(orderId);
		willDoNothing().given(cartProductRepository).deleteCartProducts(userId, cartProductIds);

		//When
		Long result = sut.order(userId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(orderId);
		then(cartProductRepository).should().findCartProducts(userId, cartProductIds);
		then(orderService).should().order(userId, serviceRequest, orderProductMap);
		then(cartProductRepository).should().deleteCartProducts(userId, cartProductIds);
	}

	@DisplayName("장바구니에서 상품을 주문할 때, 장바구니 상품을 찾지 못하면 예외를 던진다.")
	@Test
	void test_order_throwsEntityNotFoundException() {
		//Given
		List<Long> cartProductIds = List.of(1L, 2L);
		CartProductServiceRequest.Order serviceRequest =
			new CartProductServiceRequest.Order(cartProductIds, "address", "name", "01011112222");

		given(cartProductRepository.findCartProducts(userId, cartProductIds))
			.willReturn(List.of(createCartProduct(1L, 1)));

		//When & Then
		assertThatThrownBy(() -> sut.order(userId, serviceRequest))
			.isInstanceOf(EntityNotFoundException.class);
		then(cartProductRepository).should().findCartProducts(userId, cartProductIds);
		then(orderService).shouldHaveNoInteractions();
		then(cartProductRepository).shouldHaveNoMoreInteractions();
	}

	@DisplayName("장바구니에 담긴 상품의 수량을 변경한다.")
	@Test
	void test_updateCartProductQuantity() {
		//Given
		Long cartProductId = 1L;
		int updateQuantity = 1;

		given(cartProductRepository.findCartProduct(userId, cartProductId))
			.willReturn(Optional.of(createCartProduct(1L, updateQuantity)));

		//When
		CartProductResponse result = sut.updateCartProductQuantity(userId, cartProductId, updateQuantity);

		//Then
		assertThat(result.quantity()).isEqualTo(updateQuantity);
		then(cartProductRepository).should().findCartProduct(userId, cartProductId);
	}

	@DisplayName("장바구니에 담긴 상품의 수량을 변경할 때, 해당 장바구니 상품이 존재하지 않으면 예외를 던진다.")
	@Test
	void test_updateCartProductQuantity_throwsEntityNotFoundException() {
		//Given
		Long cartProductId = 1L;
		int updateQuantity = 1;

		given(cartProductRepository.findCartProduct(userId, cartProductId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.updateCartProductQuantity(userId, cartProductId, updateQuantity))
			.isInstanceOf(EntityNotFoundException.class);

		then(cartProductRepository).should().findCartProduct(userId, cartProductId);
	}

	@DisplayName("장바구니에 담긴 상품을 (복수) 삭제한다.")
	@Test
	void test_deleteCartProducts() {
		//Given
		List<Long> cartProductIds = List.of(1L);

		willDoNothing().given(cartProductRepository).deleteCartProducts(userId, cartProductIds);

		//When
		sut.deleteCartProducts(userId, cartProductIds);

		//Then
		then(cartProductRepository).should().deleteCartProducts(userId, cartProductIds);
	}

	@DisplayName("30일 전에 담긴 장바구니 상품을 삭제한다.")
	@Test
	void test_cleanUpExpiredCartProducts() {
		//Given
		Long cartProductId1 = 1L;
		Long cartProductId2 = 2L;
		CartProduct cartProduct1 = createSavedCartProduct(cartProductId1);
		CartProduct cartProduct2 = createSavedCartProduct(cartProductId2);

		given(cartProductRepository.findAllByModifiedAtBefore(any())).willReturn(List.of(cartProduct1, cartProduct2));
		willDoNothing().given(cartProductRepository).deleteAllByIdInBatch(List.of(cartProductId1, cartProductId2));

		//When
		sut.cleanUpExpiredCartProducts();

		//Then
		then(cartProductRepository).should().findAllByModifiedAtBefore(any());
		then(cartProductRepository).should().deleteAllByIdInBatch(List.of(cartProductId1, cartProductId2));
	}

	private static CartProduct createCartProduct(Long productId, int quantity) {
		return CartProduct.of(createUser(userId), createProduct(productId), quantity);
	}

	private static CartProduct createSavedCartProduct(Long cartProductId) {
		CartProduct cartProduct = CartProduct.of(createUser(userId), createProduct(1L), 1);
		ReflectionTestUtils.setField(cartProduct, "id", cartProductId);
		return cartProduct;
	}

	private static CartProduct createSavedCartProduct(Long productId, int quantity) {
		CartProduct cartProduct = CartProduct.of(createUser(userId), createProduct(productId), quantity);
		ReflectionTestUtils.setField(cartProduct, "id", 1L);
		return cartProduct;
	}
}
