package com.been.onlinestore.controller.seller;

import com.been.onlinestore.config.SecurityConfig;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 주문 (판매자)")
@Import(SecurityConfig.class)
@WebMvcTest(SellerOrderApiController.class)
class SellerOrderApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private static final long ORDER_ID = 1L;

    @DisplayName("[API][GET] 주문 리스트 조회")
    @Test
    void test_getOrderList() throws Exception {
        //Given
        long userId = 1L;
        long deliveryId = 1L;
        long orderProductId = 1L;

        //When & Then
        mvc.perform(get("/api/seller/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.data[0].userId").value(userId))
                .andExpect(jsonPath("$.data[0].delivery.id").value(deliveryId))
                .andExpect(jsonPath("$.data[0].orderProducts").isArray())
                .andExpect(jsonPath("$.data[0].orderProducts[0].id").value(orderProductId));
    }

    @DisplayName("[API][GET] 주문 리스트 조회 + 페이징")
    @Test
    void test_getOrderList_withPagination() throws Exception {
        //Given
        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 1;

        long userId = 1L;
        long deliveryId = 1L;
        long orderProductId = 1L;

        //When & Then
        mvc.perform(
                        get("/api/seller/orders")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.data[0].userId").value(userId))
                .andExpect(jsonPath("$.data[0].delivery.id").value(deliveryId))
                .andExpect(jsonPath("$.data[0].orderProducts").isArray())
                .andExpect(jsonPath("$.data[0].orderProducts[0].id").value(orderProductId))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @DisplayName("[API][GET] 주문 상세 조회")
    @Test
    void test_getOrder() throws Exception {
        //Given
        long userId = 1L;
        long deliveryId = 1L;
        long orderProductId = 1L;

        //When & Then
        mvc.perform(get("/api/seller/orders/" + ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.delivery.id").value(deliveryId))
                .andExpect(jsonPath("$.data.orderProducts").isArray())
                .andExpect(jsonPath("$.data.orderProducts[0].id").value(orderProductId));
    }

    @DisplayName("[API][PUT] 주문 상태 수정")
    @Test
    void test_updateOrderStatus() throws Exception {
        //Given
        OrderStatus orderStatus = OrderStatus.CANCEL;

        //When & Then
        mvc.perform(put("/api/seller/orders/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ORDER_ID));
    }

    @DisplayName("[API][PUT] 베송 상태 수정")
    @Test
    void test_updateDeliveryStatus() throws Exception {
        //Given
        DeliveryStatus deliveryStatus = DeliveryStatus.DELIVERING;

        //When & Then
        mvc.perform(put("/api/seller/orders/deliveries/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ORDER_ID));
    }
}
