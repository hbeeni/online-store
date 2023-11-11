package com.been.onlinestore.service;

import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.dto.CartProductDto;
import com.been.onlinestore.repository.CartProductRepository;
import com.been.onlinestore.repository.CartRepository;
import com.been.onlinestore.repository.ProductRepository;
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
import static com.been.onlinestore.util.CartTestDataUtil.createCartProductDto;
import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 장바구니 상품")
@ExtendWith(MockitoExtension.class)
class CartProductServiceTest {

    @Mock CartProductRepository cartProductRepository;
    @Mock CartRepository cartRepository;
    @Mock ProductRepository productRepository;

    @InjectMocks CartProductService sut;

    @DisplayName("장바구니 상품의 정보를 반환한다.")
    @Test
    void test_searchCartProduct() {
        //Given
        long cartProductId = 1L;
        CartProduct cartProduct = createCartProduct(cartProductId, 1L, 1L);

        given(cartProductRepository.findById(cartProductId)).willReturn(Optional.of(cartProduct));

        //When
        CartProductDto result = sut.findCartProduct(cartProductId);

        //Then
        assertThat(result).isNotNull();
        then(cartProductRepository).should().findById(cartProductId);
    }

    @DisplayName("장바구니 상품이 존재하지 않으면, 예외를 반환한다.")
    @Test
    void test_searchCartProduct_throwsIllegalArgumentException() {
        //Given
        long cartProductId = 1L;

        given(cartProductRepository.findById(cartProductId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findCartProduct(cartProductId))
                .isInstanceOf(IllegalArgumentException.class);
        then(cartProductRepository).should().findById(cartProductId);
    }

    @DisplayName("장바구니 상품을 저장하고, 저장된 상품의 정보를 반환한다.")
    @Test
    void test_addCartProduct_withNonExistentCartProduct() {
        //Given
        long cartId = 1L;
        long cartProductId = 1L;
        long productId = 1L;

        CartProductDto cartProductDto = createCartProductDto(cartProductId, cartId);
        CartProduct cartProduct = createCartProduct(cartProductId, cartId, productId);

        given(productRepository.getReferenceById(productId)).willReturn(createProduct(productId));
        given(cartProductRepository.findByCart_IdAndProduct_Id(cartId, cartProductId)).willReturn(Optional.empty());
        given(cartProductRepository.save(any())).willReturn(cartProduct);

        //When
        CartProductDto result = sut.addCartProduct(createCart(cartId, 1L), cartProductDto);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().getReferenceById(productId);
        then(cartProductRepository).should().findByCart_IdAndProduct_Id(cartId, cartProductId);
        then(cartProductRepository).should().save(any());
    }

    @DisplayName("장바구니 상품을 저장할 때, 해당 상품이 존재하면 수량을 변경하고, 정보를 반환한다.")
    @Test
    void test_addCartProduct_withExistentCartProduct() {
        //Given
        long cartId = 1L;
        long cartProductId = 1L;
        long productId = 1L;

        CartProductDto cartProductDto = createCartProductDto(cartProductId, cartId);
        CartProduct cartProduct = createCartProduct(cartProductId, cartId, productId);

        given(productRepository.getReferenceById(productId)).willReturn(createProduct(productId));
        given(cartProductRepository.findByCart_IdAndProduct_Id(cartId, cartProductId)).willReturn(Optional.of(cartProduct));

        //When
        CartProductDto result = sut.addCartProduct(createCart(cartId, 1L), cartProductDto);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().getReferenceById(productId);
        then(cartProductRepository).should().findByCart_IdAndProduct_Id(cartId, cartProductId);
    }

    @DisplayName("장바구니 상품의 수량을 수정하면, 수정된 장바구니 상품의 정보를 반환한다.")
    @Test
    void test_updateCartProductQuantity() {
        //Given
        long cartProductId = 1L;
        long cartId = 1L;
        long userId = 1L;
        int updateProductQuantity = 1;

        given(cartRepository.existsByIdAndUser_Id(cartId, userId)).willReturn(true);
        given(cartProductRepository.findByIdAndCart_Id(cartProductId, cartId)).willReturn(Optional.of(createCartProduct(cartProductId, cartId, 1L)));

        //When
        CartProductDto result = sut.updateCartProductQuantity(cartProductId, cartId, userId, updateProductQuantity);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().existsByIdAndUser_Id(cartId, userId);
        then(cartProductRepository).should().findByIdAndCart_Id(cartProductId, cartId);
    }

    @DisplayName("수정하려는 장바구니의 회원과 요청한 회원이 다르면, 예외를 던진다.")
    @Test
    void test_updateCartProductQuantity_withDifferentUser_throwsIllegalArgumentException() {
        //Given
        long cartProductId = 1L;
        long cartId = 1L;
        long userId = 1L;
        int updateProductQuantity = 1;

        given(cartRepository.existsByIdAndUser_Id(cartId, userId)).willReturn(false);

        //When & Then
        assertThatThrownBy(() -> sut.updateCartProductQuantity(cartProductId, cartId, userId, updateProductQuantity))
                .isInstanceOf(IllegalArgumentException.class);
        then(cartRepository).should().existsByIdAndUser_Id(cartId, userId);
        then(cartProductRepository).shouldHaveNoInteractions();
    }

    @DisplayName("장바구니에 없는 상품 정보를 수정하면, 예외를 던진다.")
    @Test
    void test_updateCartProductQuantity_withNonexistentCartProduct_throwsIllegalArgumentException() {
        //Given
        long cartId = 1L;
        long cartProductId = 1L;
        long userId = 1L;
        int updateProductQuantity = 1;

        given(cartRepository.existsByIdAndUser_Id(cartId, userId)).willReturn(true);
        given(cartProductRepository.findByIdAndCart_Id(cartProductId, cartId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.updateCartProductQuantity(cartProductId, cartId, userId, updateProductQuantity))
                .isInstanceOf(IllegalArgumentException.class);
        then(cartRepository).should().existsByIdAndUser_Id(cartId, userId);
        then(cartProductRepository).should().findByIdAndCart_Id(cartProductId, cartId);
    }

    @DisplayName("장바구니 상품을 삭제하면, 삭제된 장바구니 상품의 id를 반환한다.")
    @Test
    void test_deleteCartProduct() {
        //Given
        long cartId = 1L;
        long cartProductId = 1L;
        long userId = 1L;

        given(cartRepository.existsByIdAndUser_Id(cartId, userId)).willReturn(true);
        willDoNothing().given(cartProductRepository).deleteByIdAndCart_Id(cartProductId, cartId);

        //When
        Map<String, Long> result = sut.deleteCartProduct(cartProductId, cartId, userId);

        //Then
        assertThat(result).isNotNull();
        then(cartRepository).should().existsByIdAndUser_Id(cartId, userId);
        then(cartProductRepository).should().deleteByIdAndCart_Id(cartProductId, cartId);
    }
}
