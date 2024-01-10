package com.been.onlinestore.controller.admin;

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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
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

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.DeliveryStatus;
import com.been.onlinestore.domain.constant.OrderStatus;
import com.been.onlinestore.repository.querydsl.order.OrderSearchCondition;
import com.been.onlinestore.service.admin.AdminOrderService;
import com.been.onlinestore.service.dto.response.OrderProductResponse;
import com.been.onlinestore.service.dto.response.OrderResponse;

@DisplayName("어드민 API 컨트롤러 - 주문")
@Import(TestSecurityConfig.class)
@WebMvcTest(AdminOrderApiController.class)
class AdminOrderApiControllerTest extends RestDocsSupport {

	@Value("${image.path}")
	private String imagePath;

	@MockBean
	private AdminOrderService adminOrderService;

	@DisplayName("[API][GET] 주문 리스트 조회 + 검색 + 페이징")
	@Test
	void test_getOrders_withPagination() throws Exception {
		//Given
		Long searchProductId = 1L;
		OrderSearchCondition cond = OrderSearchCondition.of(null, searchProductId, null, null);

		String sortName = "createdAt";
		String direction = "desc";
		int pageNumber = 0;
		int pageSize = 10;

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
		OrderProductResponse orderProductResponse1 = OrderProductResponse.of(
			1L,
			"깐대파 500g",
			4500,
			2,
			9000,
			imagePath + "c1b2f2a2-f0b8-403a-b03b-351d1ee0bd05.jpg"
		);
		OrderProductResponse orderProductResponse2 = OrderProductResponse.of(
			2L,
			"양파 1.5kg",
			4290,
			3,
			12870,
			imagePath + "f33104ba-2e81-4b2e-91f7-658d45ec2d6d.jpg"
		);
		OrderResponse response1 = OrderResponse.of(
			1L,
			OrderResponse.OrdererResponse.of("soo", "01011111111"),
			OrderResponse.DeliveryRequestResponse.of("서울 종로구 청와대로 1", "김철수", "01011111111"),
			List.of(orderProductResponse1),
			4500,
			OrderStatus.ORDER,
			DeliveryStatus.ACCEPT,
			3000,
			now().minusDays(1),
			now().minusDays(1),
			null
		);
		OrderResponse response2 = OrderResponse.of(
			2L,
			OrderResponse.OrdererResponse.of("hee", "01022222222"),
			OrderResponse.DeliveryRequestResponse.of("서울 중구 세종대로 110 서울특별시청", "김영희", "01022222222"),
			List.of(orderProductResponse2),
			12870,
			OrderStatus.ORDER,
			DeliveryStatus.ACCEPT,
			0,
			now().minusHours(1),
			now().minusHours(1),
			null
		);
		List<OrderResponse> content = List.of(response2, response1);
		Page<OrderResponse> page = new PageImpl<>(content, pageable, content.size());

		given(adminOrderService.findOrders(cond, pageable)).willReturn(page);

		//When & Then
		String prefix = "검색할 ";

		mvc.perform(
				get("/api/admin/orders")
					.queryParam("productId", String.valueOf(searchProductId))
					.queryParam("page", String.valueOf(pageNumber))
					.queryParam("size", String.valueOf(pageSize))
					.queryParam("sort", sortName + "," + direction)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].orderId").value(response2.orderId()))
			.andExpect(jsonPath("$.data[0].orderer.uid").value(response2.orderer().uid()))
			.andExpect(jsonPath("$.data[0].deliveryRequest.deliveryAddress").isNotEmpty())
			.andExpect(jsonPath("$.data[0].orderProducts").isArray())
			.andExpect(jsonPath("$.data[0].orderProducts[0].orderProductId")
				.value(response2.orderProducts().get(0).orderProductId()))
			.andExpect(jsonPath("$.page.number").value(page.getNumber()))
			.andExpect(jsonPath("$.page.size").value(page.getSize()))
			.andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
			.andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()))
			.andDo(document(
				"admin/order/getOrders-searching",
				adminApiDescription(
					TagDescription.ORDER,
					"주문 목록 페이징 조회 (검색: 주문자 ID, 상품 ID, 배송 상태, 주문 상태)",
					"""
						모든 주문을 페이지 단위로 조회합니다.<br>
						주문자 ID, 상품 ID, 배송 상태, 주문 상태로 검색이 가능합니다.
						"""
				),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(PAGE_REQUEST_PARAM)
					.and(
						parameterWithName("ordererId")
							.description(prefix + ORDERER_ID.getDescription()).optional(),
						parameterWithName("productId")
							.description(prefix + PRODUCT_ID.getDescription()).optional(),
						parameterWithName("deliveryStatus")
							.description(prefix + ORDER_DELIVERY_STATUS.getDescription()).optional(),
						parameterWithName("orderStatus")
							.description(prefix + ORDER_STATUS.getDescription()).optional()
					),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data[].orderId").type(JsonFieldType.NUMBER)
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
					fieldWithPath("data[].orderProducts[].orderProductId").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_ID.getDescription()),
					fieldWithPath("data[].orderProducts[].productName").type(JsonFieldType.STRING)
						.description(ORDER_PRODUCT_NAME.getDescription()),
					fieldWithPath("data[].orderProducts[].price").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_PRICE.getDescription()),
					fieldWithPath("data[].orderProducts[].quantity").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data[].orderProducts[].totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_TOTAL_PRICE.getDescription()),
					fieldWithPath("data[].orderProducts[].imageUrl").type(JsonFieldType.STRING)
						.description(PRODUCT_IMAGE_URL.getDescription()),
					fieldWithPath("data[].totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_TOTAL_PRICE.getDescription()),
					fieldWithPath("data[].orderStatus").type(JsonFieldType.STRING)
						.description(ORDER_STATUS.getDescription()),
					fieldWithPath("data[].deliveryStatus").type(JsonFieldType.STRING)
						.description(ORDER_DELIVERY_STATUS.getDescription()),
					fieldWithPath("data[].deliveryFee").type(JsonFieldType.NUMBER)
						.description(DELIVERY_FEE.getDescription()),
					fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
						.description(CREATED_AT.getDescription()),
					fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING)
						.description(MODIFIED_AT.getDescription()),
					fieldWithPath("data[].deliveredAt").type(JsonFieldType.VARIES)
						.description(DELIVERED_AT.getDescription())
				).and(PAGE_INFO)
			));
		then(adminOrderService).should().findOrders(cond, pageable);
	}

	@DisplayName("[API][GET] 주문 상세 조회")
	@Test
	void test_getOrder() throws Exception {
		//Given
		OrderProductResponse orderProductResponse = OrderProductResponse.of(
			1L,
			"깐대파 500g",
			4500,
			2,
			9000,
			imagePath + "c1b2f2a2-f0b8-403a-b03b-351d1ee0bd05.jpg"
		);
		OrderResponse response = OrderResponse.of(
			1L,
			OrderResponse.OrdererResponse.of("soo", "01011111111"),
			OrderResponse.DeliveryRequestResponse.of("서울 종로구 청와대로 1", "김철수", "01011111111"),
			List.of(orderProductResponse),
			4500,
			OrderStatus.ORDER,
			DeliveryStatus.ACCEPT,
			3000,
			now().minusDays(1),
			now().minusDays(1),
			null
		);

		given(adminOrderService.findOrder(response.orderId())).willReturn(response);

		//When & Then
		mvc.perform(get("/api/admin/orders/{orderId}", response.orderId()))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.orderId").value(response.orderId()))
			.andExpect(jsonPath("$.data.orderer.uid").value(response.orderer().uid()))
			.andExpect(jsonPath("$.data.deliveryRequest.deliveryAddress").isNotEmpty())
			.andExpect(jsonPath("$.data.orderProducts").isArray())
			.andExpect(jsonPath("$.data.orderProducts[0].orderProductId")
				.value(response.orderProducts().get(0).orderProductId()))
			.andDo(document(
				"admin/order/getOrder",
				adminApiDescription(
					TagDescription.ORDER,
					"주문 상세 조회",
					"주문 ID(orderId)로 주문 정보를 조회합니다."
				),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("orderId").description(ORDER_ID.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.orderId").type(JsonFieldType.NUMBER)
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
					fieldWithPath("data.orderProducts[].orderProductId").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_ID.getDescription()),
					fieldWithPath("data.orderProducts[].productName").type(JsonFieldType.STRING)
						.description(ORDER_PRODUCT_NAME.getDescription()),
					fieldWithPath("data.orderProducts[].price").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_PRICE.getDescription()),
					fieldWithPath("data.orderProducts[].quantity").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data.orderProducts[].totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_PRODUCT_TOTAL_PRICE.getDescription()),
					fieldWithPath("data.orderProducts[].imageUrl").type(JsonFieldType.STRING)
						.description(PRODUCT_IMAGE_URL.getDescription()),
					fieldWithPath("data.deliveryStatus").type(JsonFieldType.STRING)
						.description(ORDER_DELIVERY_STATUS.getDescription()),
					fieldWithPath("data.deliveryFee").type(JsonFieldType.NUMBER)
						.description(DELIVERY_FEE.getDescription()),
					fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
						.description(ORDER_TOTAL_PRICE.getDescription()),
					fieldWithPath("data.orderStatus").type(JsonFieldType.STRING)
						.description(ORDER_STATUS.getDescription()),
					fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
						.description(CREATED_AT.getDescription()),
					fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING)
						.description(MODIFIED_AT.getDescription()),
					fieldWithPath("data.deliveredAt").type(JsonFieldType.VARIES)
						.description(DELIVERED_AT.getDescription())
				)
			));
		then(adminOrderService).should().findOrder(response.orderId());
	}

	@DisplayName("[API][PUT] 베송 상태 수정 - 상품 준비 중")
	@Test
	void test_prepareOrders() throws Exception {
		//Given
		long orderId = 1L;

		given(adminOrderService.startPreparing(orderId)).willReturn(orderId);

		//When & Then
		mvc.perform(
				put("/api/admin/orders/{orderId}/deliveries/prepare", orderId)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").exists())
			.andExpect(jsonPath("$.data.id").value(orderId))
			.andDo(document(
				"admin/order/prepareOrders",
				adminApiDescription(
					TagDescription.ORDER,
					"상품 준비 중 처리",
					"""
						주문의 배송 상태를 상품 준비 중(PREPARING)으로 변경합니다.<br>
						단, 배송 상태가 결제 완료(ACCEPT)일 때만 상품 준비 중으로 변경할 수 있습니다.
						"""
				),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("orderId").description(ORDER_ID.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER).description(ORDER_ID.getDescription())
				)
			));
		then(adminOrderService).should().startPreparing(orderId);
	}

	@DisplayName("[API][PUT] 베송 상태 수정 - 배송 시작")
	@Test
	void test_startDelivery() throws Exception {
		//Given
		long orderId = 1L;

		given(adminOrderService.startDelivery(orderId)).willReturn(orderId);

		//When & Then
		mvc.perform(
				put("/api/admin/orders/{orderId}/deliveries/start", orderId)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").exists())
			.andExpect(jsonPath("$.data.id").value(orderId))
			.andDo(document(
				"admin/order/startDelivery",
				adminApiDescription(
					TagDescription.ORDER,
					"배송 중 처리",
					"""
						주문의 배송 상태를 배송 중(DELIVERING)으로 변경합니다.<br>
						단, 배송 상태가 상품 준비 중(PREPARING)일 때만 배송 중으로 변경할 수 있습니다.
						"""
				),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("orderId").description(ORDER_ID.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER).description(ORDER_ID.getDescription())
				)
			));
		then(adminOrderService).should().startDelivery(orderId);
	}

	@DisplayName("[API][PUT] 베송 상태 수정 - 배송 완료")
	@Test
	void test_completeDelivery() throws Exception {
		//Given
		long orderId = 1L;

		given(adminOrderService.completeDelivery(orderId)).willReturn(orderId);

		//When & Then
		mvc.perform(
				put("/api/admin/orders/{orderId}/deliveries/complete", orderId)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").exists())
			.andExpect(jsonPath("$.data.id").value(orderId))
			.andDo(document(
				"admin/order/completeDelivery",
				adminApiDescription(
					TagDescription.ORDER,
					"배송 완료 처리",
					"""
						주문의 배송 상태를 배송 완료(COMPLETED)로 변경합니다.<br>
						단, 배송 상태가 배송 중(DELIVERING)일 때만 배송 완료로 변경할 수 있습니다.
						"""
				),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("orderId").description(ORDER_ID.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER).description(ORDER_ID.getDescription())
				)
			));
		then(adminOrderService).should().completeDelivery(orderId);
	}

	@DisplayName("[API][PUT] 주문 취소")
	@Test
	void test_cancelOrder() throws Exception {
		//Given
		long orderId = 1L;

		given(adminOrderService.cancelOrder(orderId)).willReturn(orderId);

		//When & Then
		mvc.perform(put("/api/admin/orders/{orderId}/cancel", orderId))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(orderId))
			.andDo(document(
				"admin/order/cancelOrder",
				adminApiDescription(
					TagDescription.ORDER,
					"주문 취소",
					"""
						주문을 취소합니다.<br>
						단, 배송 상태가 결제 완료(ACCEPT)인 주문만 취소가 가능합니다.
						"""),
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
		then(adminOrderService).should().cancelOrder(orderId);
	}
}
