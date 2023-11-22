package com.been.onlinestore.controller.seller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.service.OrderService;
import com.been.onlinestore.service.response.OrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.been.onlinestore.util.OrderTestDataUtil.createOrderResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 주문")
@Import(TestSecurityConfig.class)
@WebMvcTest(SellerOrderApiController.class)
class SellerOrderApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private OrderService orderService;

    @WithUserDetails("seller")
    @DisplayName("[API][GET] 주문 리스트 조회 + 페이징")
    @Test
    void test_getOrderList_withPagination() throws Exception {
        //Given
        long orderId = 1L;
        long sellerId = TestSecurityConfig.SELLER_ID;
        long orderProductId = 1L;

        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        OrderResponse response = createOrderResponse(orderId, "uid", orderProductId);
        Page<OrderResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        given(orderService.findOrdersBySeller(sellerId, pageable)).willReturn(page);

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
                .andExpect(jsonPath("$.data[0].id").value(response.id()))
                .andExpect(jsonPath("$.data[0].orderer.uid").value(response.orderer().uid()))
                .andExpect(jsonPath("$.data[0].deliveryRequest.deliveryAddress").isNotEmpty())
                .andExpect(jsonPath("$.data[0].orderProducts").isArray())
                .andExpect(jsonPath("$.data[0].orderProducts[0].id").value(orderProductId))
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
        then(orderService).should().findOrdersBySeller(sellerId, pageable);
    }

    @WithUserDetails("seller")
    @DisplayName("[API][GET] 주문 상세 조회")
    @Test
    void test_getOrder() throws Exception {
        //Given
        long orderId = 1L;
        long sellerId = TestSecurityConfig.SELLER_ID;
        long orderProductId = 1L;

        OrderResponse response = createOrderResponse(orderId, "uid", orderProductId);
        given(orderService.findOrderBySeller(orderId, sellerId)).willReturn(response);

        //When & Then
        mvc.perform(get("/api/seller/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(response.id()))
                .andExpect(jsonPath("$.data.orderer.uid").value(response.orderer().uid()))
                .andExpect(jsonPath("$.data.deliveryRequest.deliveryAddress").isNotEmpty())
                .andExpect(jsonPath("$.data.orderProducts").isArray())
                .andExpect(jsonPath("$.data.orderProducts[0].id").value(orderProductId));
    }
}
