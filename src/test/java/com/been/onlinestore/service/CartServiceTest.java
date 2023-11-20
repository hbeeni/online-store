package com.been.onlinestore.service;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.request.CartProductServiceRequest;
import com.been.onlinestore.service.response.CartWithCartProductsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static com.been.onlinestore.util.CartTestDataUtil.createCart;
import static com.been.onlinestore.util.CartTestDataUtil.createCartProduct;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 장바구니")
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartProductService cartProductService;

    @InjectMocks private CartService sut;

    @DisplayName("회원의 현재 장바구니가 있을 경우, 장바구니 정보를 반환한다.")
    @Test
    void test_findCart() {
        //Given
        long userId = 1L;
        given(cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)).willReturn(Optional.of(createCart(1L, userId)));

        //When
        Optional<CartWithCartProductsResponse> result = sut.findCart(userId);

        //Then
        assertThat(result).isPresent();
        then(cartRepository).should().findFirstByUser_IdOrderByCreatedAtDesc(userId);
    }

    @DisplayName("회원의 현재 장바구니가 없을 경우, 빈 Optional을 반환한다.")
    @Test
    void test_findCart_returnEmptyOptional() {
        //Given
        long userId = 1L;
        given(cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)).willReturn(Optional.empty());

        //When
        Optional<CartWithCartProductsResponse> result = sut.findCart(userId);

        //Then
        assertThat(result).isEmpty();
        then(cartRepository).should().findFirstByUser_IdOrderByCreatedAtDesc(userId);
    }

    @DisplayName("장바구니에 상품을 추가할 때, 현재 존재하는 장바구니가 없으면 장바구니를 생성 후 상품을 추가하고, 저장된 장바구니의 id를 반환한다.")
    @Test
    void test_addCartProductToCart_whenCartDoesntExist() {
        //Given
        long cartId = 1L;
        long userId = 1L;
        long productId = 1L;

        User user = createUser(userId);
        CartProductServiceRequest.Create serviceRequest = new CartProductServiceRequest.Create(productId, 10);

        given(cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)).willReturn(Optional.empty());
        given(userRepository.getReferenceById(userId)).willReturn(user);
        given(cartProductService.addCartProduct(any(Cart.class), eq(serviceRequest))).willReturn(createCartProduct(1L, cartId, productId));
        given(cartRepository.save(any(Cart.class))).willReturn(createCart(cartId, userId));

        //When
        Map<String, Long> result = sut.addCartProductToCart(userId, serviceRequest);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().findFirstByUser_IdOrderByCreatedAtDesc(userId);
        then(userRepository).should().getReferenceById(userId);
        then(cartProductService).should().addCartProduct(any(Cart.class), eq(serviceRequest));
        then(cartRepository).should().save(any(Cart.class));
    }

    @DisplayName("장바구니에 상품을 추가할 때, 현재 존재하는 장바구니가 있으면 상품을 추가하고, 장바구니의 id를 반환한다.")
    @Test
    void test_addCartProductToCart_whenCartExists() {
        //Given
        long cartId = 1L;
        long userId = 1L;
        long productId = 1L;

        CartProductServiceRequest.Create serviceRequest = new CartProductServiceRequest.Create(productId, 10);

        given(cartRepository.findFirstByUser_IdOrderByCreatedAtDesc(userId)).willReturn(Optional.of(createCart(cartId, userId)));
        given(cartProductService.addCartProduct(any(Cart.class), eq(serviceRequest))).willReturn(createCartProduct(1L, cartId, productId));

        //When
        Map<String, Long> result = sut.addCartProductToCart(userId, serviceRequest);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().findFirstByUser_IdOrderByCreatedAtDesc(userId);
        then(cartProductService).should().addCartProduct(any(Cart.class), eq(serviceRequest));
        then(cartRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("장바구니를 삭제하면, 장바구니 상품을 모두 삭제하고, 삭제된 장바구니의 id를 반환한다.")
    @Test
    void test_deleteCart() {
        //Given
        long cartId = 1L;
        long userId = 1L;

        willDoNothing().given(cartRepository).deleteByIdAndUser_Id(cartId, userId);

        //When
        Long result = sut.deleteCart(userId, cartId);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().deleteByIdAndUser_Id(cartId, userId);
    }
}
