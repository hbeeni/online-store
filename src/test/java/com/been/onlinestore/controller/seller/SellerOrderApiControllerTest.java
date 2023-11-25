package com.been.onlinestore.controller.seller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.service.OrderService;
import com.been.onlinestore.service.response.OrderProductResponse;
import com.been.onlinestore.service.response.OrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static com.been.onlinestore.controller.restdocs.FieldDescription.CREATED_AT;
import static com.been.onlinestore.controller.restdocs.FieldDescription.DELIVERY_REQUEST_ADDRESS;
import static com.been.onlinestore.controller.restdocs.FieldDescription.DELIVERY_REQUEST_RECEIVER_NAME;
import static com.been.onlinestore.controller.restdocs.FieldDescription.DELIVERY_REQUEST_RECEIVER_PHONE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.MODIFIED_AT;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDERER_PHONE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDERER_UID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_ID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_DELIVERED_AT;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_DELIVERY_FEE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_DELIVERY_STATUS;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_ID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_NAME;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_PRICE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_QUANTITY;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_PRODUCT_TOTAL_PRICE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_STATUS;
import static com.been.onlinestore.controller.restdocs.FieldDescription.ORDER_TOTAL_PRICE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_ID;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.PAGE_INFO;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.PAGE_REQUEST_PARAM;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.sellerApiDescription;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static java.time.LocalDateTime.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 주문")
@Import(TestSecurityConfig.class)
@WebMvcTest(SellerOrderApiController.class)
class SellerOrderApiControllerTest extends RestDocsSupport {

    @MockBean private OrderService orderService;

    @WithUserDetails("seller")
    @DisplayName("[API][GET] 주문 리스트 조회 + 페이징")
    @Test
    void test_getOrderList_withPagination() throws Exception {
        //Given
        long sellerId = TestSecurityConfig.SELLER_ID;

        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        OrderProductResponse orderProductResponse1 = OrderProductResponse.of(
                2L,
                "체크 셔츠",
                23000,
                3,
                69000,
                DeliveryStatus.ACCEPT,
                0,
                null
        );
        OrderProductResponse orderProductResponse2 = OrderProductResponse.of(
                1L,
                "꽃무늬 셔츠",
                12000,
                2,
                24000,
                DeliveryStatus.ACCEPT,
                3000,
                null
        );
        OrderResponse response1 = OrderResponse.of(
                1L,
                OrderResponse.OrdererResponse.of("user1", "01012345678"),
                OrderResponse.DeliveryRequestResponse.of("서울 종로구 청와대로 1", "user1", "01012345678"),
                List.of(orderProductResponse1),
                69000,
                OrderStatus.ORDER,
                now().minusDays(1),
                now().minusDays(1)
        );
        OrderResponse response2 = OrderResponse.of(
                2L,
                OrderResponse.OrdererResponse.of("user2", "01011112222"),
                OrderResponse.DeliveryRequestResponse.of("서울 중구 세종대로 110 서울특별시청", "user2", "01011112222"),
                List.of(orderProductResponse2),
                24000,
                OrderStatus.ORDER,
                now().minusHours(1),
                now().minusHours(1)
        );
        List<OrderResponse> content = List.of(response2, response1);
        Page<OrderResponse> page = new PageImpl<>(content, pageable, content.size());

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
                .andExpect(jsonPath("$.data[0].id").value(response2.id()))
                .andExpect(jsonPath("$.data[0].orderer.uid").value(response2.orderer().uid()))
                .andExpect(jsonPath("$.data[0].deliveryRequest.deliveryAddress").isNotEmpty())
                .andExpect(jsonPath("$.data[0].orderProducts").isArray())
                .andExpect(jsonPath("$.data[0].orderProducts[0].id").value(response2.orderProducts().get(0).id()))
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()))
                .andDo(document(
                        "seller/order/getOrders",
                        sellerApiDescription(TagDescription.ORDER, "주문 페이징 조회"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(PAGE_REQUEST_PARAM),
                        responseFields(
                                RestDocsUtils.STATUS,
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description(ORDER_ID.getDescription()),
                                fieldWithPath("data[].orderer.uid").type(JsonFieldType.STRING).description(ORDERER_UID.getDescription()),
                                fieldWithPath("data[].orderer.phone").type(JsonFieldType.STRING).description(ORDERER_PHONE.getDescription()),
                                fieldWithPath("data[].deliveryRequest.deliveryAddress").type(JsonFieldType.STRING).description(DELIVERY_REQUEST_ADDRESS.getDescription()),
                                fieldWithPath("data[].deliveryRequest.receiverName").type(JsonFieldType.STRING).description(DELIVERY_REQUEST_RECEIVER_NAME.getDescription()),
                                fieldWithPath("data[].deliveryRequest.receiverPhone").type(JsonFieldType.STRING).description(DELIVERY_REQUEST_RECEIVER_PHONE.getDescription()),
                                fieldWithPath("data[].orderProducts[].id").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_ID.getDescription()),
                                fieldWithPath("data[].orderProducts[].productName").type(JsonFieldType.STRING).description(ORDER_PRODUCT_NAME.getDescription()),
                                fieldWithPath("data[].orderProducts[].price").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_PRICE.getDescription()),
                                fieldWithPath("data[].orderProducts[].quantity").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_QUANTITY.getDescription()),
                                fieldWithPath("data[].orderProducts[].totalPrice").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_TOTAL_PRICE.getDescription()),
                                fieldWithPath("data[].orderProducts[].deliveryStatus").type(JsonFieldType.STRING).description(ORDER_PRODUCT_DELIVERY_STATUS.getDescription()),
                                fieldWithPath("data[].orderProducts[].deliveryFee").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_DELIVERY_FEE.getDescription()),
                                fieldWithPath("data[].orderProducts[].deliveredAt").type(JsonFieldType.VARIES).description(ORDER_PRODUCT_DELIVERED_AT.getDescription()),
                                fieldWithPath("data[].totalPrice").type(JsonFieldType.NUMBER).description(ORDER_TOTAL_PRICE.getDescription()),
                                fieldWithPath("data[].orderStatus").type(JsonFieldType.STRING).description(ORDER_STATUS.getDescription()),
                                fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description(CREATED_AT.getDescription()),
                                fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING).description(MODIFIED_AT.getDescription())
                        ).and(PAGE_INFO)
                ));
        then(orderService).should().findOrdersBySeller(sellerId, pageable);
    }

    @WithUserDetails("seller")
    @DisplayName("[API][GET] 주문 상세 조회")
    @Test
    void test_getOrder() throws Exception {
        //Given
        long sellerId = TestSecurityConfig.SELLER_ID;

        OrderProductResponse orderProductResponse = OrderProductResponse.of(
                2L,
                "체크 셔츠",
                23000,
                3,
                69000,
                DeliveryStatus.ACCEPT,
                0,
                null
        );
        OrderResponse response = OrderResponse.of(
                1L,
                OrderResponse.OrdererResponse.of("user1", "01012345678"),
                OrderResponse.DeliveryRequestResponse.of("서울 종로구 청와대로 1", "user1", "01012345678"),
                List.of(orderProductResponse),
                69000,
                OrderStatus.ORDER,
                now().minusDays(1),
                now().minusDays(1)
        );

        given(orderService.findOrderBySeller(response.id(), sellerId)).willReturn(response);

        //When & Then
        mvc.perform(get("/api/seller/orders/{orderId}", response.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(response.id()))
                .andExpect(jsonPath("$.data.orderer.uid").value(response.orderer().uid()))
                .andExpect(jsonPath("$.data.deliveryRequest.deliveryAddress").isNotEmpty())
                .andExpect(jsonPath("$.data.orderProducts").isArray())
                .andExpect(jsonPath("$.data.orderProducts[0].id").value(response.orderProducts().get(0).id()))
                .andDo(document(
                        "seller/order/getOrder",
                        sellerApiDescription(TagDescription.ORDER, "주문 상세 조회"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description(PRODUCT_ID.getDescription())
                        ),
                        responseFields(
                                RestDocsUtils.STATUS,
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description(ORDER_ID.getDescription()),
                                fieldWithPath("data.orderer.uid").type(JsonFieldType.STRING).description(ORDERER_UID.getDescription()),
                                fieldWithPath("data.orderer.phone").type(JsonFieldType.STRING).description(ORDERER_PHONE.getDescription()),
                                fieldWithPath("data.deliveryRequest.deliveryAddress").type(JsonFieldType.STRING).description(DELIVERY_REQUEST_ADDRESS.getDescription()),
                                fieldWithPath("data.deliveryRequest.receiverName").type(JsonFieldType.STRING).description(DELIVERY_REQUEST_RECEIVER_NAME.getDescription()),
                                fieldWithPath("data.deliveryRequest.receiverPhone").type(JsonFieldType.STRING).description(DELIVERY_REQUEST_RECEIVER_PHONE.getDescription()),
                                fieldWithPath("data.orderProducts[].id").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_ID.getDescription()),
                                fieldWithPath("data.orderProducts[].productName").type(JsonFieldType.STRING).description(ORDER_PRODUCT_NAME.getDescription()),
                                fieldWithPath("data.orderProducts[].price").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_PRICE.getDescription()),
                                fieldWithPath("data.orderProducts[].quantity").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_QUANTITY.getDescription()),
                                fieldWithPath("data.orderProducts[].totalPrice").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_TOTAL_PRICE.getDescription()),
                                fieldWithPath("data.orderProducts[].deliveryStatus").type(JsonFieldType.STRING).description(ORDER_PRODUCT_DELIVERY_STATUS.getDescription()),
                                fieldWithPath("data.orderProducts[].deliveryFee").type(JsonFieldType.NUMBER).description(ORDER_PRODUCT_DELIVERY_FEE.getDescription()),
                                fieldWithPath("data.orderProducts[].deliveredAt").type(JsonFieldType.VARIES).description(ORDER_PRODUCT_DELIVERED_AT.getDescription()),
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description(ORDER_TOTAL_PRICE.getDescription()),
                                fieldWithPath("data.orderStatus").type(JsonFieldType.STRING).description(ORDER_STATUS.getDescription()),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description(CREATED_AT.getDescription()),
                                fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING).description(MODIFIED_AT.getDescription())
                        )
                ));
    }
}
