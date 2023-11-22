package com.been.onlinestore.controller.seller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.service.OrderProductService;
import com.been.onlinestore.service.response.DeliveryStatusChangeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static com.been.onlinestore.util.OrderTestDataUtil.createDeliveryStatusChangeResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 주문 상품")
@Import(TestSecurityConfig.class)
@WebMvcTest(SellerOrderProductApiController.class)
class SellerOrderProductApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private OrderProductService orderProductService;

    @WithUserDetails("seller")
    @DisplayName("[API][PUT] 베송 상태 수정 - 상품 준비 중")
    @Test
    void test_prepareProducts() throws Exception {
        //Given
        long sellerId = TestSecurityConfig.SELLER_ID;
        long orderProductId1 = 1L;
        long orderProductId2 = 2L;
        Set<Long> orderProductIds = Set.of(orderProductId1, orderProductId2);

        DeliveryStatusChangeResponse response = createDeliveryStatusChangeResponse(orderProductIds);

        given(orderProductService.startPreparing(orderProductIds, sellerId)).willReturn(response);

        //When & Then
        mvc.perform(
                        put("/api/seller/order-products/deliveries/prepare")
                                .queryParam("orderProductIds", String.valueOf(orderProductId1), String.valueOf(orderProductId2))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.notFountOrderProductIds").doesNotExist())
                .andExpect(jsonPath("$.data.succeededOrderProductIds").exists())
                .andExpect(jsonPath("$.data.failedOrderProductIds").doesNotExist());
        then(orderProductService).should().startPreparing(orderProductIds, sellerId);
    }

    @WithUserDetails("seller")
    @DisplayName("[API][PUT] 베송 상태 수정 - 배송 시작")
    @Test
    void test_startDelivery() throws Exception {
        //Given
        long sellerId = TestSecurityConfig.SELLER_ID;
        long orderProductId1 = 1L;
        long orderProductId2 = 2L;
        Set<Long> orderProductIds = Set.of(orderProductId1, orderProductId2);

        DeliveryStatusChangeResponse response = createDeliveryStatusChangeResponse(orderProductIds);

        given(orderProductService.startDelivery(orderProductIds, sellerId)).willReturn(response);

        //When & Then
        mvc.perform(
                        put("/api/seller/order-products/deliveries/start")
                                .queryParam("orderProductIds", String.valueOf(orderProductId1), String.valueOf(orderProductId2))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.notFountOrderProductIds").doesNotExist())
                .andExpect(jsonPath("$.data.succeededOrderProductIds").exists())
                .andExpect(jsonPath("$.data.failedOrderProductIds").doesNotExist());
        then(orderProductService).should().startDelivery(orderProductIds, sellerId);
    }

    @WithUserDetails("seller")
    @DisplayName("[API][PUT] 베송 상태 수정 - 배송 완료")
    @Test
    void test_completeDelivery() throws Exception {
        //Given
        long sellerId = TestSecurityConfig.SELLER_ID;
        long orderProductId1 = 1L;
        long orderProductId2 = 2L;
        Set<Long> orderProductIds = Set.of(orderProductId1, orderProductId2);

        DeliveryStatusChangeResponse response = createDeliveryStatusChangeResponse(orderProductIds);

        given(orderProductService.completeDelivery(orderProductIds, sellerId)).willReturn(response);

        //When & Then
        mvc.perform(
                        put("/api/seller/order-products/deliveries/complete")
                                .queryParam("orderProductIds", String.valueOf(orderProductId1), String.valueOf(orderProductId2))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.notFountOrderProductIds").doesNotExist())
                .andExpect(jsonPath("$.data.succeededOrderProductIds").exists())
                .andExpect(jsonPath("$.data.failedOrderProductIds").doesNotExist());
        then(orderProductService).should().completeDelivery(orderProductIds, sellerId);
    }
}
