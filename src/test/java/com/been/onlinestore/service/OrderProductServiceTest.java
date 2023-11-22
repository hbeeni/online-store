package com.been.onlinestore.service;

import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.repository.OrderProductRepository;
import com.been.onlinestore.service.response.DeliveryStatusChangeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static com.been.onlinestore.util.OrderTestDataUtil.createOrderProduct;
import static org.assertj.core.api.Assertions.assertThat;
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
        Set<Long> orderProductIds = Set.of(orderProductId);
        OrderProduct orderProduct = createOrderProduct(orderProductId);

        given(orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId)).willReturn(List.of(orderProduct));
        given(orderProductRepository.bulkStartPreparing(orderProductIds)).willReturn(orderProductIds.size());

        //When
        DeliveryStatusChangeResponse response = sut.startPreparing(orderProductIds, sellerId);

        //Then
        assertThat(response).isNotNull();
        then(orderProductRepository).should().findAllByIdAndSellerId(orderProductIds, sellerId);
        then(orderProductRepository).should().bulkStartPreparing(orderProductIds);
    }

    @DisplayName("[판매자] 주문 상품의 배송을 시작한다.")
    @Test
    void test_startDelivery() {
        //Given
        long orderProductId = 1L;
        long sellerId = 1L;
        Set<Long> orderProductIds = Set.of(orderProductId);
        OrderProduct orderProduct = createOrderProduct(orderProductId);
        ReflectionTestUtils.setField(orderProduct.getDelivery(), "deliveryStatus", DeliveryStatus.PREPARING);

        given(orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId)).willReturn(List.of(orderProduct));
        given(orderProductRepository.bulkStartDelivery(orderProductIds)).willReturn(orderProductIds.size());

        //When
        DeliveryStatusChangeResponse response = sut.startDelivery(orderProductIds, sellerId);

        //Then
        assertThat(response).isNotNull();
        then(orderProductRepository).should().findAllByIdAndSellerId(orderProductIds, sellerId);
        then(orderProductRepository).should().bulkStartDelivery(orderProductIds);
    }

    @DisplayName("[판매자] 주문 상품의 배송을 완료한다.")
    @Test
    void test_completeDelivery() {
        //Given
        long orderProductId = 1L;
        long sellerId = 1L;
        Set<Long> orderProductIds = Set.of(orderProductId);
        OrderProduct orderProduct = createOrderProduct(orderProductId);
        ReflectionTestUtils.setField(orderProduct.getDelivery(), "deliveryStatus", DeliveryStatus.DELIVERING);

        given(orderProductRepository.findAllByIdAndSellerId(orderProductIds, sellerId)).willReturn(List.of(orderProduct));
        given(orderProductRepository.bulkCompleteDelivery(orderProductIds)).willReturn(orderProductIds.size());

        //When
        DeliveryStatusChangeResponse response = sut.completeDelivery(orderProductIds, sellerId);

        //Then
        assertThat(response).isNotNull();
        then(orderProductRepository).should().findAllByIdAndSellerId(orderProductIds, sellerId);
        then(orderProductRepository).should().bulkCompleteDelivery(orderProductIds);
    }
}
