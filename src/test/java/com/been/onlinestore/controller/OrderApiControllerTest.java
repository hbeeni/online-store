package com.been.onlinestore.controller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static java.time.LocalDateTime.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

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

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.OrderRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.service.OrderService;
import com.been.onlinestore.service.response.OrderProductResponse;
import com.been.onlinestore.service.response.OrderResponse;

@DisplayName("API 컨트롤러 - 주문")
@Import(TestSecurityConfig.class)
@WebMvcTest(OrderApiController.class)
class OrderApiControllerTest extends RestDocsSupport {

	@MockBean
	private OrderService orderService;

	@WithUserDetails
	@DisplayName("[API][GET] 주문 리스트 조회 + 페이징 - 생성일 내림차순")
	@Test
	void test_getOrderList_withPagination() throws Exception {
		//Given
		long userId = TestSecurityConfig.USER_ID;
		long orderProductId = 1L;

		String sortName = "createdAt";
		String direction = "desc";
		int pageNumber = 0;
		int pageSize = 10;

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
		OrderProductResponse orderProductResponse1 = OrderProductResponse.of(
			1L,
			"꽃무늬 셔츠",
			12000,
			2,
			24000,
			DeliveryStatus.ACCEPT,
			3000,
			null
		);
		OrderProductResponse orderProductResponse2 = OrderProductResponse.of(
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
			OrderResponse.OrdererResponse.of("user", "01012345678"),
			OrderResponse.DeliveryRequestResponse.of("서울 종로구 청와대로 1", "user", "01011112222"),
			List.of(orderProductResponse1, orderProductResponse2),
			93000,
			OrderStatus.ORDER,
			now(),
			now()
		);
		List<OrderResponse> content = List.of(response);
		Page<OrderResponse> page = new PageImpl<>(content, pageable, content.size());

		given(orderService.findOrdersByOrderer(userId, pageable)).willReturn(page);

		//When & Then
		mvc.perform(
				get("/api/orders")
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
			.andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()))
			.andDo(document(
				"user/order/getOrders",
				userApiDescription(TagDescription.ORDER, "주문 페이징 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(PAGE_REQUEST_PARAM),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
						.description(ORDER_ID.getDescription()),
					fieldWithPath("data[].orderer.uid").type(JsonFieldType.STRING)
						.description(ORDERER_UID.getDescription()),
					fieldWithPath("data[].orderer.phone").type(JsonFieldType.STRING)
						.description(ORDERER_PHONE.getDescription()),
					fieldWithPath("data[].deliveryRequest.deliveryAddress").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_ADDRESS.getDescription()),
					fieldWithPath("data[].deliveryRequest.receiverName").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_RECEIVER_NAME.getDescription()),
					fieldWithPath("data[].deliveryRequest.receiverPhone").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_RECEIVER_PHONE.getDescription()),
					fieldWithPath("data[].orderProducts[].id").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_ID.getDescription()),
					fieldWithPath("data[].orderProducts[].productName").type(JsonFieldType.STRING)
						.description(ORDER_PRODUCT_NAME.getDescription()),
					fieldWithPath("data[].orderProducts[].price").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_PRICE.getDescription()),
					fieldWithPath("data[].orderProducts[].quantity").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data[].orderProducts[].totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_TOTAL_PRICE.getDescription()),
					fieldWithPath("data[].orderProducts[].deliveryStatus").type(JsonFieldType.STRING)
						.description(ORDER_PRODUCT_DELIVERY_STATUS.getDescription()),
					fieldWithPath("data[].orderProducts[].deliveryFee").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_DELIVERY_FEE.getDescription()),
					fieldWithPath("data[].orderProducts[].deliveredAt").type(JsonFieldType.VARIES)
						.description(ORDER_PRODUCT_DELIVERED_AT.getDescription()),
					fieldWithPath("data[].totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_TOTAL_PRICE.getDescription()),
					fieldWithPath("data[].orderStatus").type(JsonFieldType.STRING)
						.description(ORDER_STATUS.getDescription()),
					fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
						.description(CREATED_AT.getDescription()),
					fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING)
						.description(MODIFIED_AT.getDescription())
				).and(PAGE_INFO)
			));
		then(orderService).should().findOrdersByOrderer(userId, pageable);
	}

	@WithUserDetails
	@DisplayName("[API][GET] 주문 상세 조회")
	@Test
	void test_getOrder() throws Exception {
		//Given
		long userId = TestSecurityConfig.USER_ID;

		OrderProductResponse orderProductResponse1 = OrderProductResponse.of(
			1L,
			"꽃무늬 셔츠",
			12000,
			2,
			24000,
			DeliveryStatus.ACCEPT,
			3000,
			null
		);
		OrderProductResponse orderProductResponse2 = OrderProductResponse.of(
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
			OrderResponse.OrdererResponse.of("user", "01012345678"),
			OrderResponse.DeliveryRequestResponse.of("서울 종로구 청와대로 1", "user", "01011112222"),
			List.of(orderProductResponse1, orderProductResponse2),
			93000,
			OrderStatus.ORDER,
			now(),
			now()
		);
		given(orderService.findOrderByOrderer(response.id(), userId)).willReturn(response);

		//When & Then
		mvc.perform(get("/api/orders/{orderId}", response.id()))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(response.id()))
			.andExpect(jsonPath("$.data.orderer.uid").value(response.orderer().uid()))
			.andExpect(jsonPath("$.data.deliveryRequest.deliveryAddress").isNotEmpty())
			.andExpect(jsonPath("$.data.orderProducts").isArray())
			.andExpect(jsonPath("$.data.orderProducts[0].id").value(response.orderProducts().get(0).id()))
			.andDo(document(
				"user/order/getOrder",
				userApiDescription(TagDescription.ORDER, "주문 상세 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("orderId").description(ORDER_ID.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(ORDER_ID.getDescription()),
					fieldWithPath("data.orderer.uid").type(JsonFieldType.STRING)
						.description(ORDERER_UID.getDescription()),
					fieldWithPath("data.orderer.phone").type(JsonFieldType.STRING)
						.description(ORDERER_PHONE.getDescription()),
					fieldWithPath("data.deliveryRequest.deliveryAddress").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_ADDRESS.getDescription()),
					fieldWithPath("data.deliveryRequest.receiverName").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_RECEIVER_NAME.getDescription()),
					fieldWithPath("data.deliveryRequest.receiverPhone").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_RECEIVER_PHONE.getDescription()),
					fieldWithPath("data.orderProducts[].id").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_ID.getDescription()),
					fieldWithPath("data.orderProducts[].productName").type(JsonFieldType.STRING)
						.description(ORDER_PRODUCT_NAME.getDescription()),
					fieldWithPath("data.orderProducts[].price").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_PRICE.getDescription()),
					fieldWithPath("data.orderProducts[].quantity").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data.orderProducts[].totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_TOTAL_PRICE.getDescription()),
					fieldWithPath("data.orderProducts[].deliveryStatus").type(JsonFieldType.STRING)
						.description(ORDER_PRODUCT_DELIVERY_STATUS.getDescription()),
					fieldWithPath("data.orderProducts[].deliveryFee").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_DELIVERY_FEE.getDescription()),
					fieldWithPath("data.orderProducts[].deliveredAt").type(JsonFieldType.VARIES)
						.description(ORDER_PRODUCT_DELIVERED_AT.getDescription()),
					fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_TOTAL_PRICE.getDescription()),
					fieldWithPath("data.orderStatus").type(JsonFieldType.STRING)
						.description(ORDER_STATUS.getDescription()),
					fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
						.description(CREATED_AT.getDescription()),
					fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING)
						.description(MODIFIED_AT.getDescription())
				)
			));
		then(orderService).should().findOrderByOrderer(response.id(), userId);
	}

	@WithUserDetails
	@DisplayName("[API][POST] 주문하기")
	@Test
	void test_order() throws Exception {
		//Given
		long orderId = 1L;
		long userId = TestSecurityConfig.USER_ID;
		OrderRequest request = new OrderRequest(Map.of(1L, 10), "서울 종로구 청와대로 1", "user", "01011112222");

		given(orderService.order(userId, request.toServiceRequest())).willReturn(orderId);

		//When & Then
		mvc.perform(
				post("/api/orders")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(orderId))
			.andDo(document(
				"user/order/order",
				userApiDescription(TagDescription.ORDER, "주문하기"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					subsectionWithPath("productIdToQuantity").type(JsonFieldType.OBJECT)
						.description("상품 시퀀스 : 수량 맵"),
					fieldWithPath("deliveryAddress").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_ADDRESS.getDescription()),
					fieldWithPath("receiverName").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_RECEIVER_NAME.getDescription()),
					fieldWithPath("receiverPhone").type(JsonFieldType.STRING)
						.description(DELIVERY_REQUEST_RECEIVER_PHONE.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(ADD.getDescription() + ORDER_ID.getDescription())
				)
			));
		then(orderService).should().order(userId, request.toServiceRequest());
	}

	@WithUserDetails
	@DisplayName("[API][PUT] 주문 취소")
	@Test
	void test_cancelOrder() throws Exception {
		//Given
		long orderId = 1L;
		long userId = TestSecurityConfig.USER_ID;

		given(orderService.cancelOrder(orderId, userId)).willReturn(orderId);

		//When & Then
		mvc.perform(put("/api/orders/{orderId}/cancel", orderId))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(orderId))
			.andDo(document(
				"user/order/cancelOrder",
				userApiDescription(TagDescription.ORDER, "주문 취소하기"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("orderId").description(ORDER_ID.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description("취소된 " + ORDER_ID.getDescription())
				)
			));
		then(orderService).should().cancelOrder(orderId, userId);
	}
}
