package com.been.onlinestore.service;

import com.been.onlinestore.dto.response.OrderResponse;
import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.been.onlinestore.util.OrderTestDataUtil.createOrder;
import static com.been.onlinestore.util.OrderTestDataUtil.createOrderDto;
import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 주문")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private OrderService sut;

    @DisplayName("[일반 회원] 모든 주문 내역을 조회하면, 주문내역 페이지를 반환한다.")
    @Test
    void test_searchOrders() {
        //Given
        long ordererId = 1L;
        Pageable pageable = PageRequest.of(1, 2);
        given(orderRepository.findAllOrdersByOrderer(ordererId, pageable)).willReturn(Page.empty());

        //When
        Page<OrderResponse> result = sut.findOrdersByOrderer(ordererId, pageable);

        //Then
        assertThat(result).isNotNull();
        then(orderRepository).should().findAllOrdersByOrderer(ordererId, pageable);
    }

    @DisplayName("[일반 회원] 주문을 조회하면, 주문 상세 정보를 반환한다.")
    @Test
    void test_searchOrder() {
        //Given
        long orderId = 1L;
        long ordererId = 1L;
        given(orderRepository.findOrderByOrderer(orderId, ordererId)).willReturn(Optional.of(createOrder(orderId)));

        //When
        OrderResponse result = sut.findOrderByOrderer(orderId, ordererId);

        //Then
        assertThat(result).isNotNull();
        then(orderRepository).should().findOrderByOrderer(orderId, ordererId);
    }

    @DisplayName("[일반 회원] 주문을 조회할 때, 주문을 찾지 못하면 예외를 던진다.")
    @Test
    void test_searchOrder_throwsIllegalArgumentException() {
        //Given
        long orderId = 1L;
        long ordererId = 1L;
        given(orderRepository.findOrderByOrderer(orderId, ordererId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findOrderByOrderer(orderId, ordererId))
                .isInstanceOf(IllegalArgumentException.class);
        then(orderRepository).should().findOrderByOrderer(orderId, ordererId);
    }

    @DisplayName("[일반 회원] 주문을 하면, 주문 상품과 배송 정보를 함께 저장한 후, 저장된 주문의 id를 반환한다.")
    @Test
    void test_order() {
        //Given
        long orderId = 1L;
        long ordererId = 1L;
        long productId = 1L;
        int quantity = 3;
        List<Long> productIds = List.of(productId);
        List<Integer> quantities = List.of(quantity);

        given(userRepository.getReferenceById(ordererId)).willReturn(createUser(ordererId));
        given(productRepository.findAllOnSaleById(Set.of(productId))).willReturn(List.of(createProduct(productId)));
        given(orderRepository.save(any())).willReturn(createOrder(orderId));

        //When
        Long result = sut.order(ordererId, productIds, quantities, createOrderDto(orderId));

        //Then
        assertThat(result).isEqualTo(orderId);
        then(userRepository).should().getReferenceById(ordererId);
        then(productRepository).should().findAllOnSaleById(Set.of(productId));
        then(orderRepository).should().save(any());
    }

    @DisplayName("[일반 회원] 주문을 취소하면, 취소된 주문의 id를 반환한다.")
    @Test
    void test_cancelOrder() {
        //Given
        long orderId = 1L;
        long ordererId = 1L;
        given(orderRepository.findByIdAndOrdererId(orderId, ordererId)).willReturn(Optional.of(createOrder(ordererId)));

        //When
        Long result = sut.cancelOrder(orderId, ordererId);

        //Then
        assertThat(result).isEqualTo(orderId);
        then(orderRepository).should().findByIdAndOrdererId(orderId, ordererId);
    }

    @DisplayName("[일반 회원] 주문을 취소할 때, 취소할 주문을 찾지 못하면 예외를 던진다.")
    @Test
    void test_cancelOrder_throwsIllegalArgumentException() {
        //Given
        long orderId = 1L;
        long ordererId = 1L;
        given(orderRepository.findByIdAndOrdererId(orderId, ordererId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.cancelOrder(orderId, ordererId))
                .isInstanceOf(IllegalArgumentException.class);
        then(orderRepository).should().findByIdAndOrdererId(orderId, ordererId);
    }

    @DisplayName("[판매자] 모든 주문 내역을 조회하면, 주문내역 페이지를 반환한다.")
    @Test
    void test_seller_searchOrders() {
        //Given
        long sellerId = 1L;
        Pageable pageable = PageRequest.of(1, 2);

        given(orderRepository.findAllOrdersBySeller(sellerId, pageable)).willReturn(Page.empty());

        //When
        Page<OrderResponse> result = sut.findOrdersBySeller(sellerId, pageable);

        //Then
        assertThat(result).isNotNull();
        then(orderRepository).should().findAllOrdersBySeller(sellerId, pageable);
    }

    @DisplayName("[판매자] 주문을 조회하면, 주문 정보를 반환한다.")
    @Test
    void test_seller_searchOrder() {
        //Given
        long orderId = 1L;
        long sellerId = 1L;
        given(orderRepository.findOrderBySeller(orderId, sellerId)).willReturn(Optional.of(createOrder(orderId)));

        //When
        OrderResponse result = sut.findOrderBySeller(orderId, sellerId);

        //Then
        assertThat(result).isNotNull();
        then(orderRepository).should().findOrderBySeller(orderId, sellerId);
    }

    @DisplayName("[판매자] 주문을 조회할 때, 해당 주문이 없으면 예외를 던진다.")
    @Test
    void test_seller_searchOrder_throwIllegalArgumentException() {
        //Given
        long orderId = 1L;
        long sellerId = 1L;
        given(orderRepository.findOrderBySeller(orderId, sellerId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findOrderBySeller(orderId, sellerId))
                .isInstanceOf(IllegalArgumentException.class);
        then(orderRepository).should().findOrderBySeller(orderId, sellerId);
    }
}
