package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.DeliveryRequest;
import com.been.onlinestore.domain.Order;
import com.been.onlinestore.domain.OrderProduct;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.dto.OrderDto;
import com.been.onlinestore.dto.response.OrderResponse;
import com.been.onlinestore.repository.OrderRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Transactional
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrdersByOrderer(Long ordererId, Pageable pageable) {
        return orderRepository.findAllOrdersByOrderer(ordererId, pageable)
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderByOrderer(Long orderId, Long ordererId) {
        return orderRepository.findOrderByOrderer(orderId, ordererId)
                .map(OrderResponse::from)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> findOrdersBySeller(Long sellerId, Pageable pageable) {
        return orderRepository.findAllOrdersBySeller(sellerId, pageable)
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse findOrderBySeller(Long orderId, Long sellerId) {
        return orderRepository.findOrderBySeller(orderId, sellerId)
                .map(OrderResponse::from)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
    }

    public Long order(Long ordererId, List<Long> productIds, List<Integer> quantities, OrderDto dto) {
        User orderer = userRepository.getReferenceById(ordererId);

        Map<Long, Integer> productIdToQuantityMap = makeProductIdToQuantityMap(productIds, quantities);
        List<Product> products = productRepository.findAllOnSaleById(productIdToQuantityMap.keySet());

        if (productIdToQuantityMap.keySet().size() != products.size()) {
            throw new IllegalArgumentException(ErrorMessages.NOT_SALE_PRODUCT.getMessage());
        }

        Map<Product, Integer> productToQuantityMap = convertToProductToQuantityMap(productIdToQuantityMap, products);
        List<OrderProduct> orderProducts = createOrderProducts(productToQuantityMap);

        Order order = Order.of(
                orderer,
                DeliveryRequest.of(dto.deliveryRequestDto().deliveryAddress(), dto.deliveryRequestDto().receiverName(), dto.deliveryRequestDto().receiverPhone()),
                orderer.getPhone(),
                OrderStatus.ORDER
        );
        order.addOrderProducts(orderProducts);
        return orderRepository.save(order).getId();
    }

    public Long cancelOrder(Long orderId, Long ordererId) {
        Order order = orderRepository.findByIdAndOrdererId(orderId, ordererId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_FOUND_ORDER.getMessage()));
        order.cancel();
        return order.getId();
    }

    private Map<Long, Integer> makeProductIdToQuantityMap(List<Long> productIds, List<Integer> quantities) {
        if (productIds.size() != quantities.size()) {
            throw new IllegalArgumentException(ErrorMessages.NOT_MATCH_ORDER_PRODUCT_COUNT_TO_QUANTITY_COUNT.getMessage());
        }
        return IntStream.range(0, productIds.size())
                .boxed()
                .collect(Collectors.toMap(productIds::get, quantities::get, Integer::sum));
    }

    private Map<Product, Integer> convertToProductToQuantityMap(Map<Long, Integer> productIdToQuantityMap, List<Product> products) {
        return IntStream.range(0, products.size())
                .boxed()
                .collect(Collectors.toMap(
                        products::get,
                        i -> productIdToQuantityMap.get(products.get(i).getId()))
                );
    }

    private List<OrderProduct> createOrderProducts(Map<Product, Integer> productToQuantityMap) {
        return productToQuantityMap.entrySet().stream()
                .map(entry -> OrderProduct.of(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();
    }
}
