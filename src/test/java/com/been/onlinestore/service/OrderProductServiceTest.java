package com.been.onlinestore.service;

import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.repository.OrderProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.been.onlinestore.util.OrderTestDataUtil.createOrderProduct;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 주문 상품")
@ExtendWith(MockitoExtension.class)
class OrderProductServiceTest {

    @Mock private OrderProductRepository orderProductRepository;

    @InjectMocks private OrderProductService sut;

    @DisplayName("[판매자] 주문 상품의 상품 준비를 시작한다.")
    @Test
    void test_startPreparing() {
        //Given
        long orderProductId = 1L;
        long sellerId = 1L;
        List<Long> orderProductIds = List.of(orderProductId);
        given(orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId)).willReturn(List.of(createOrderProduct(orderProductId, DeliveryStatus.ACCEPT)));

        //When
        sut.startPreparing(orderProductIds, sellerId);

        //Then
        then(orderProductRepository).should().findAllByIdAndSellerId(orderProductIds, sellerId);
    }

    @DisplayName("[판매자] 주문 상품의 배송을 시작한다.")
    @Test
    void test_startDelivery() {
        //Given
        long orderProductId = 1L;
        long sellerId = 1L;
        List<Long> orderProductIds = List.of(orderProductId);
        given(orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId)).willReturn(List.of(createOrderProduct(orderProductId, DeliveryStatus.PREPARING)));

        //When
        sut.startDelivery(orderProductIds, sellerId);

        //Then
        then(orderProductRepository).should().findAllByIdAndSellerId(orderProductIds, sellerId);
    }

    @DisplayName("[판매자] 주문 상품의 배송을 완료한다.")
    @Test
    void test_completeDelivery() {
        //Given
        long orderProductId = 1L;
        long sellerId = 1L;
        List<Long> orderProductIds = List.of(orderProductId);
        given(orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId)).willReturn(List.of(createOrderProduct(orderProductId, DeliveryStatus.DELIVERING)));

        //When
        sut.completeDelivery(orderProductIds, sellerId);

        //Then
        then(orderProductRepository).should().findAllByIdAndSellerId(orderProductIds, sellerId);
    }
}
