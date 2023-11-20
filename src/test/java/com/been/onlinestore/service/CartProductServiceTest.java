package com.been.onlinestore.service;

import com.been.onlinestore.domain.Cart;
import com.been.onlinestore.domain.CartProduct;
import com.been.onlinestore.repository.CartProductRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.service.request.CartProductServiceRequest;
import com.been.onlinestore.service.response.CartProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.been.onlinestore.util.CartTestDataUtil.createCart;
import static com.been.onlinestore.util.CartTestDataUtil.createCartProduct;
import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 장바구니 상품")
@ExtendWith(MockitoExtension.class)
class CartProductServiceTest {

    @Mock CartProductRepository cartProductRepository;
    @Mock ProductRepository productRepository;

    @InjectMocks CartProductService sut;

    @DisplayName("장바구니 상품을 저장하고, 저장된 상품의 id를 반환한다.")
    @Test
    void test_addCartProduct_withNonExistentCartProduct() {
        //Given
        long cartId = 1L;
        long cartProductId = 1L;
        long productId = 1L;

        Cart cart = createCart(cartId, 1L);
        CartProductServiceRequest.Create serviceRequest = new CartProductServiceRequest.Create(productId, 10);

        given(productRepository.getReferenceById(serviceRequest.productId())).willReturn(createProduct(productId));
        given(cartProductRepository.findByCart_IdAndProduct_Id(cartId, cartProductId)).willReturn(Optional.empty());

        //When
        CartProduct result = sut.addCartProduct(cart, serviceRequest);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().getReferenceById(serviceRequest.productId());
        then(cartProductRepository).should().findByCart_IdAndProduct_Id(cartId, cartProductId);
    }

    @DisplayName("장바구니 상품을 저장할 때, 해당 상품이 존재하면 수량을 변경하고, 정보를 반환한다.")
    @Test
    void test_addCartProduct_withExistentCartProduct() {
        //Given
        long cartProductId = 1L;
        long cartId = 1L;
        long productId = 1L;

        Cart cart = createCart(cartId, 1L);
        CartProductServiceRequest.Create serviceRequest = new CartProductServiceRequest.Create(productId, 10);
        CartProduct cartProduct = createCartProduct(cartProductId, cartId, productId);

        given(productRepository.getReferenceById(productId)).willReturn(createProduct(productId));
        given(cartProductRepository.findByCart_IdAndProduct_Id(cartId, cartProductId)).willReturn(Optional.of(cartProduct));

        //When
        CartProduct result = sut.addCartProduct(cart, serviceRequest);

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

        given(cartProductRepository.findCartProduct(cartProductId, cartId, userId)).willReturn(Optional.of(createCartProduct(cartProductId, cartId, 1L)));

        //When
        CartProductResponse result = sut.updateCartProductQuantity(cartProductId, cartId, userId, updateProductQuantity);

        //Then
        assertThat(result).isNotNull();
        then(cartProductRepository).should().findCartProduct(cartProductId, cartId, userId);
    }

    @DisplayName("장바구니에 없는 상품 정보를 수정하면, 예외를 던진다.")
    @Test
    void test_updateCartProductQuantity_withNonexistentCartProduct_throwsEntityNotfoundException() {
        //Given
        long cartProductId = 1L;
        long cartId = 1L;
        long userId = 1L;
        int updateProductQuantity = 1;

        given(cartProductRepository.findCartProduct(cartProductId, cartId, userId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.updateCartProductQuantity(cartProductId, cartId, userId, updateProductQuantity))
                .isInstanceOf(EntityNotFoundException.class);
        then(cartProductRepository).should().findCartProduct(cartProductId, cartId, userId);
    }

    @DisplayName("장바구니 상품을 삭제하면, 삭제된 장바구니 상품의 id를 반환한다.")
    @Test
    void test_deleteCartProduct() {
        //Given
        List<Long> cartProductIds = List.of(1L);
        long cartId = 1L;
        long userId = 1L;

        willDoNothing().given(cartProductRepository).deleteCartProducts(cartProductIds, cartId, userId);

        //When
        CartProductService.IdsMap result = sut.deleteCartProducts(cartProductIds, cartId, userId);

        //Then
        assertThat(result).isNotNull();
        then(cartProductRepository).should().deleteCartProducts(cartProductIds, cartId, userId);
    }
}
