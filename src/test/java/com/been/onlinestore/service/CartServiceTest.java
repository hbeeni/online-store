package com.been.onlinestore.service;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.dto.CartProductDto;
import com.been.onlinestore.dto.CartWithCartProductsDto;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static com.been.onlinestore.util.CartTestDataUtil.createCart;
import static com.been.onlinestore.util.CartTestDataUtil.createCartProductDto;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    void test_searchCart() {
        //Given
        long userId = 1L;
        given(cartRepository.findByUser_Id(userId)).willReturn(Optional.of(createCart(1L, userId)));

        //When
        Optional<CartWithCartProductsDto> result = sut.findCart(userId);

        //Then
        assertThat(result).isPresent();
        then(cartRepository).should().findByUser_Id(userId);
    }

    @DisplayName("회원의 현재 장바구니가 없을 경우, 빈 Optional을 반환한다.")
    @Test
    void test_searchCart_returnEmptyOptional() {
        //Given
        long userId = 1L;
        given(cartRepository.findByUser_Id(userId)).willReturn(Optional.empty());

        //When
        Optional<CartWithCartProductsDto> result = sut.findCart(userId);

        //Then
        assertThat(result).isEmpty();
        then(cartRepository).should().findByUser_Id(userId);
    }

    @DisplayName("장바구니에 상품을 추가할 때, 현재 존재하는 장바구니가 없으면 장바구니를 생성 후 상품을 추가하고, 저장된 장바구니의 id를 반환한다.")
    @Test
    void test_addCartProductToCart_whenCartDoesntExist() {
        //Given
        long cartId = 1L;
        long userId = 1L;
        long cartProductId = 1L;

        Cart cart = createCart(cartId, userId);
        CartProductDto cartProductDto = createCartProductDto(cartProductId, cartId);

        given(cartRepository.findByUser_Id(userId)).willReturn(Optional.empty());
        given(userRepository.getReferenceById(userId)).willReturn(createUser(userId));
        given(cartRepository.save(any())).willReturn(cart);
        given(cartProductService.addCartProduct(cart, cartProductDto)).willReturn(cartProductDto);

        //When
        Map<String, Long> result = sut.addCartProductToCart(userId, cartProductDto);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().findByUser_Id(userId);
        then(userRepository).should().getReferenceById(userId);
        then(cartRepository).should().save(any());
        then(cartProductService).should().addCartProduct(cart, cartProductDto);
    }

    @DisplayName("장바구니에 상품을 추가할 때, 현재 존재하는 장바구니가 있으면 상품을 추가하고, 장바구니의 id를 반환한다.")
    @Test
    void test_addCartProductToCart_whenCartExists() {
        //Given
        long cartId = 1L;
        long userId = 1L;
        long cartProductId = 1L;

        Cart cart = createCart(cartId, userId);
        CartProductDto cartProductDto = createCartProductDto(cartProductId, cartId);

        given(cartRepository.findByUser_Id(userId)).willReturn(Optional.of(cart));
        given(cartProductService.addCartProduct(cart, cartProductDto)).willReturn(cartProductDto);

        //When
        Map<String, Long> result = sut.addCartProductToCart(userId, cartProductDto);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().findByUser_Id(userId);
        then(cartProductService).should().addCartProduct(cart, cartProductDto);
    }

    @DisplayName("장바구니를 삭제하면, 장바구니 상품을 모두 삭제하고, 삭제된 장바구니의 id를 반환한다.")
    @Test
    void test_deleteCart() {
        //Given
        long cartId = 1L;
        long userId = 1L;

        given(cartRepository.existsByIdAndUser_Id(cartId, userId)).willReturn(true);
        willDoNothing().given(cartProductService).deleteCartProducts(cartId);
        willDoNothing().given(cartRepository).deleteById(cartId);

        //When
        Long result = sut.deleteCart(userId, cartId);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().existsByIdAndUser_Id(cartId, userId);
        then(cartProductService).should().deleteCartProducts(cartId);
        then(cartRepository).should().deleteById(cartId);
    }
}
