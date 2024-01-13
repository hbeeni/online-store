package com.been.onlinestore.service.admin;

import static com.been.onlinestore.util.OrderTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.exception.CustomException;
import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.querydsl.order.OrderSearchCondition;
import com.been.onlinestore.service.dto.response.OrderResponse;

@DisplayName("어드민 비즈니스 로직 - 주문")
@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private AdminOrderService sut;

	@DisplayName("주문 내역을 검색과 함께 조회하면, 해당 주문내역 페이지를 반환한다.")
	@Test
	void test_findOrders_withSearchCondition() {
		//Given
		OrderSearchCondition cond =
			OrderSearchCondition.of(null, null, DeliveryStatus.ACCEPT, OrderStatus.ORDER);
		Pageable pageable = PageRequest.of(0, 10);

		given(orderRepository.findOrdersForAdmin(cond, pageable)).willReturn(Page.empty());

		//When
		Page<OrderResponse> result = sut.findOrders(cond, pageable);

		//Then
		assertThat(result).isNotNull();
		then(orderRepository).should().findOrdersForAdmin(cond, pageable);
	}

	@DisplayName("주문을 조회하면, 주문 정보를 반환한다.")
	@Test
	void test_findOrder() {
		//Given
		long orderId = 1L;

		given(orderRepository.findOrderByIdForAdmin(orderId)).willReturn(Optional.of(createOrder(orderId)));

		//When
		OrderResponse result = sut.findOrder(orderId);

		//Then
		assertThat(result).isNotNull();
		then(orderRepository).should().findOrderByIdForAdmin(orderId);
	}

	@DisplayName("주문을 조회할 때, 해당 주문이 없으면 예외를 던진다.")
	@Test
	void test_findOrder_throwCustomException() {
		//Given
		long orderId = 1L;

		given(orderRepository.findOrderByIdForAdmin(orderId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.findOrder(orderId))
			.isInstanceOf(CustomException.class);
		then(orderRepository).should().findOrderByIdForAdmin(orderId);
	}

	@DisplayName("해당 주문의 상품 준비를 시작한다.")
	@Test
	void test_startPreparing() {
		//Given
		long orderId = 1L;

		given(orderRepository.findByIdWithDetails(orderId)).willReturn(Optional.of(createOrder(orderId)));

		//When
		Long response = sut.startPreparing(orderId);

		//Then
		assertThat(response).isNotNull();
		then(orderRepository).should().findByIdWithDetails(orderId);
	}

	@DisplayName("해당 주문의 배송을 시작한다.")
	@Test
	void test_startDelivery() {
		//Given
		long orderId = 1L;

		given(orderRepository.findByIdWithDetails(orderId))
			.willReturn(Optional.of(createOrder(orderId, DeliveryStatus.PREPARING)));

		//When
		Long response = sut.startDelivery(orderId);

		//Then
		assertThat(response).isNotNull();
		then(orderRepository).should().findByIdWithDetails(orderId);
	}

	@DisplayName("해당 주문의 배송을 완료한다.")
	@Test
	void test_completeDelivery() {
		//Given
		long orderId = 1L;
		Order order = createOrder(orderId, DeliveryStatus.DELIVERING);

		given(orderRepository.findByIdWithDetails(orderId)).willReturn(Optional.of(order));

		//When
		Long response = sut.completeDelivery(orderId);

		//Then
		assertThat(response).isNotNull();
		assertThat(order).extracting("delivery").extracting("deliveredAt").isNotNull();
		then(orderRepository).should().findByIdWithDetails(orderId);
	}

	@DisplayName("주문을 취소하면, 취소된 주문의 id를 반환한다.")
	@Test
	void test_cancelOrder() {
		//Given
		long orderId = 1L;

		given(orderRepository.findByIdWithDetails(orderId)).willReturn(Optional.of(createOrder(orderId)));

		//When
		Long result = sut.cancelOrder(orderId);

		//Then
		assertThat(result).isEqualTo(orderId);
		then(orderRepository).should().findByIdWithDetails(orderId);
	}

	@DisplayName("주문을 취소할 때, 취소할 주문을 찾지 못하면 예외를 던진다.")
	@Test
	void test_cancelOrder_throwsCustomException() {
		//Given
		long orderId = 1L;

		given(orderRepository.findByIdWithDetails(orderId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.cancelOrder(orderId))
			.isInstanceOf(CustomException.class);
		then(orderRepository).should().findByIdWithDetails(orderId);
	}
}
