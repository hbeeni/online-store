package com.been.onlinestore.controller.user;

import com.been.onlinestore.config.SecurityConfig;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 주문 (일반)")
@Import(SecurityConfig.class)
@WebMvcTest(OrderApiController.class)
class OrderApiControllerTest {

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
        mvc.perform(get("/api/user/orders"))
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

    @DisplayName("[API][GET] 주문 리스트 조회 + 페이징 - 생성일 내림차순")
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
                        get("/api/user/orders")
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
        mvc.perform(get("/api/user/orders/" + ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.orderId").value(ORDER_ID))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.delivery.id").value(deliveryId))
                .andExpect(jsonPath("$.data.orderProducts").isArray())
                .andExpect(jsonPath("$.data.orderProducts[0].id").value(orderProductId));
    }

    @DisplayName("[API][POST] 주문하기")
    @Test
    void test_order() throws Exception {
        //Given

        //When & Then
        mvc.perform(post("/api/user/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ORDER_ID));
    }

    @DisplayName("[API][PUT] 주문 취소")
    @Test
    void test_cancelOrder() throws Exception {
        //Given
        OrderStatus orderStatus = OrderStatus.CANCEL;

        //When & Then
        mvc.perform(put("/api/user/orders/" + ORDER_ID + "/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ORDER_ID));
    }
}
