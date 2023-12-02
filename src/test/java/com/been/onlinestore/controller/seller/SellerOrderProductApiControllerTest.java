package com.been.onlinestore.controller.seller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.service.OrderProductService;
import com.been.onlinestore.service.response.DeliveryStatusChangeResponse;

@DisplayName("API 컨트롤러 - 주문 상품")
@Import(TestSecurityConfig.class)
@WebMvcTest(SellerOrderProductApiController.class)
class SellerOrderProductApiControllerTest extends RestDocsSupport {

	public static final String NOT_FOUND = "존재하지 않는 주문 상품 시퀀스";
	public static final String SUCCEEDED = "변경 성공 주문 상품 시퀀스";
	public static final String FAILED = "변경 실패 주문 상품 시퀀스";

	@MockBean
	private OrderProductService orderProductService;

	@WithUserDetails("seller")
	@DisplayName("[API][PUT] 베송 상태 수정 - 상품 준비 중")
	@Test
	void test_prepareProducts() throws Exception {
		//Given
		long sellerId = TestSecurityConfig.SELLER_ID;
		long orderProductId1 = 1L;
		long orderProductId2 = 2L;
		Set<Long> orderProductIds = Set.of(orderProductId1, orderProductId2);

		DeliveryStatusChangeResponse response = DeliveryStatusChangeResponse.of(
			null,
			orderProductIds,
			null
		);

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
			.andExpect(jsonPath("$.data.failedOrderProductIds").doesNotExist())
			.andDo(document(
				"seller/orderProduct/prepareProducts",
				userApiDescription(TagDescription.ORDER_PRODUCT, "주문상품 상품 준비 중 처리"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(
					parameterWithName("orderProductIds").description(
						ORDER_PRODUCT_ID.getDescription() + " (복수)")
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.notFountOrderProductIds").type(JsonFieldType.ARRAY)
						.description(NOT_FOUND)
						.optional(),
					fieldWithPath("data.succeededOrderProductIds").type(JsonFieldType.ARRAY)
						.description(SUCCEEDED)
						.optional(),
					fieldWithPath("data.failedOrderProductIds").type(JsonFieldType.ARRAY)
						.description(FAILED)
						.optional()
				)
			));
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

		DeliveryStatusChangeResponse response = DeliveryStatusChangeResponse.of(
			null,
			orderProductIds,
			null
		);

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
			.andExpect(jsonPath("$.data.failedOrderProductIds").doesNotExist())
			.andDo(document(
				"seller/orderProduct/startDelivery",
				userApiDescription(TagDescription.ORDER_PRODUCT, "주문상품 배송 중 처리"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(
					parameterWithName("orderProductIds").description(
						ORDER_PRODUCT_ID.getDescription() + " (복수)")
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.notFountOrderProductIds").type(JsonFieldType.ARRAY)
						.description(NOT_FOUND)
						.optional(),
					fieldWithPath("data.succeededOrderProductIds").type(JsonFieldType.ARRAY)
						.description(SUCCEEDED)
						.optional(),
					fieldWithPath("data.failedOrderProductIds").type(JsonFieldType.ARRAY)
						.description(FAILED)
						.optional()
				)
			));
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

		DeliveryStatusChangeResponse response = DeliveryStatusChangeResponse.of(
			null,
			orderProductIds,
			null
		);

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
			.andExpect(jsonPath("$.data.failedOrderProductIds").doesNotExist())
			.andDo(document(
				"seller/orderProduct/completeDelivery",
				userApiDescription(TagDescription.ORDER_PRODUCT, "주문상품 배송 완료 처리"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(
					parameterWithName("orderProductIds").description(
						ORDER_PRODUCT_ID.getDescription() + " (복수)")
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.notFountOrderProductIds").type(JsonFieldType.ARRAY)
						.description(NOT_FOUND)
						.optional(),
					fieldWithPath("data.succeededOrderProductIds").type(JsonFieldType.ARRAY)
						.description(SUCCEEDED)
						.optional(),
					fieldWithPath("data.failedOrderProductIds").type(JsonFieldType.ARRAY)
						.description(FAILED)
						.optional()
				)
			));
		then(orderProductService).should().completeDelivery(orderProductIds, sellerId);
	}
}
