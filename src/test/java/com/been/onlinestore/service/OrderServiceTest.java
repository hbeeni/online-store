package com.been.onlinestore.service;

import static com.been.onlinestore.service.dto.request.OrderServiceRequest.*;
import static com.been.onlinestore.util.OrderTestDataUtil.*;
import static com.been.onlinestore.util.ProductTestDataUtil.*;
import static com.been.onlinestore.util.UserTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.request.OrderServiceRequest;
import com.been.onlinestore.service.dto.response.OrderResponse;

@DisplayName("비즈니스 로직 - 주문")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private OrderService sut;

	@DisplayName("모든 주문 내역을 조회하면, 주문내역 페이지를 반환한다.")
	@Test
	void test_findOrdersByOrderer() {
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

	@DisplayName("주문을 조회하면, 주문 상세 정보를 반환한다.")
	@Test
	void test_findOrderByOrderer() {
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

	@DisplayName("주문을 조회할 때, 주문을 찾지 못하면 예외를 던진다.")
	@Test
	void test_findOrderByOrderer_throwsEntityNotFoundException() {
		//Given
		long orderId = 1L;
		long ordererId = 1L;
		given(orderRepository.findOrderByOrderer(orderId, ordererId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.findOrderByOrderer(orderId, ordererId))
			.isInstanceOf(EntityNotFoundException.class);
		then(orderRepository).should().findOrderByOrderer(orderId, ordererId);
	}

	@DisplayName("주문을 하면, 주문 상품과 배송 정보를 함께 저장한 후, 저장된 주문의 id를 반환한다.")
	@Test
	void test_order() {
		//Given
		long orderId = 1L;
		long ordererId = 1L;
		long productId = 1L;
		List<OrderProductServiceRequest> orderProductServiceRequests = List.of(
			OrderProductServiceRequest.of(productId, 1)
		);
		OrderServiceRequest serviceRequest =
			new OrderServiceRequest(orderProductServiceRequests, "address", "name", "01011112222");

		given(productRepository.findAllOnSaleById(Set.of(productId))).willReturn(List.of(createProduct(productId)));
		given(userRepository.findById(ordererId)).willReturn(Optional.of(createUser(ordererId)));
		given(orderRepository.save(any())).willReturn(createOrder(orderId));

		//When
		Long result = sut.order(ordererId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(orderId);
		then(productRepository).should().findAllOnSaleById(Set.of(productId));
		then(userRepository).should().findById(ordererId);
		then(orderRepository).should().save(any());
	}

	@DisplayName("주문을 취소하면, 취소된 주문의 id를 반환한다.")
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

	@DisplayName("주문을 취소할 때, 취소할 주문을 찾지 못하면 예외를 던진다.")
	@Test
	void test_cancelOrder_throwsEntityNotFoundException() {
		//Given
		long orderId = 1L;
		long ordererId = 1L;
		given(orderRepository.findByIdAndOrdererId(orderId, ordererId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.cancelOrder(orderId, ordererId))
			.isInstanceOf(EntityNotFoundException.class);
		then(orderRepository).should().findByIdAndOrdererId(orderId, ordererId);
	}
}
