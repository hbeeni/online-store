package com.been.onlinestore.util;

import com.been.onlinestore.domain.Delivery;
import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import org.springframework.test.util.ReflectionTestUtils;

import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
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

    public static OrderProduct createOrderProduct(Long id) {
        return createOrderProduct(id, DeliveryStatus.ACCEPT);
    }

    public static OrderProduct createOrderProduct(Long id, DeliveryStatus deliveryStatus) {
        OrderProduct orderProduct = OrderProduct.of(
                null,
                createProduct(1L),
                Delivery.of(deliveryStatus, 3000, now()),
                10
        );
        ReflectionTestUtils.setField(orderProduct, "id", id);
        return orderProduct;
    }
}
