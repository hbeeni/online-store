package com.been.onlinestore.util;

import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.dto.DeliveryRequestDto;
import com.been.onlinestore.dto.OrderDto;
import com.been.onlinestore.dto.OrderProductDto;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static java.time.LocalDateTime.now;

public class OrderTestDataUtil {

    public static Order createOrder(Long id) {
        Order order = Order.of(
                createUser(),
                DeliveryRequest.of("delivery address", "receiver name", "01012341234"),
                "01012341234",
                OrderStatus.ORDER
        );
        ReflectionTestUtils.setField(order, "id", id);
        return order;
    }

    public static OrderDto createOrderDto(Long orderId) {
        return OrderDto.of(
                1L,
                "01012341234",
                DeliveryRequestDto.of(1L, "delivery Address", "receiver", "01011112222"),
                List.of(OrderProductDto.of(1L, orderId, 1L, 10000, 10, 3000)),
                100000,
                OrderStatus.ORDER,
                now(),
                now()
        );
    }
}
