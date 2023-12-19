package com.been.onlinestore.controller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.CartProductRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.service.CartProductService;
import com.been.onlinestore.service.dto.request.CartProductServiceRequest;
import com.been.onlinestore.service.dto.response.CartProductResponse;
import com.been.onlinestore.service.dto.response.CartResponse;

@DisplayName("API 컨트롤러 - 장바구니")
@Import(TestSecurityConfig.class)
@WebMvcTest(CartApiController.class)
class CartApiControllerTest extends RestDocsSupport {

	private static final Long userId = TestSecurityConfig.USER_ID;

	@MockBean
	private CartProductService cartProductService;

	@WithUserDetails
	@DisplayName("[API][GET] 장바구니 상품 리스트 조회")
	@Test
	void test_getProductsInCart() throws Exception {
		//Given
		CartProductResponse cartProductResponse1 = CartProductResponse.of(
			1L,
			"깐대파 500g",
			4500,
			2,
			9000
		);
		CartProductResponse cartProductResponse2 = CartProductResponse.of(
			2L,
			"양파 1.5kg",
			4290,
			1,
			4290
		);
		List<CartProductResponse> cartProducts = List.of(cartProductResponse1, cartProductResponse2);
		CartResponse response = CartResponse.of(13290, 3000, cartProducts);

		given(cartProductService.findCartProducts(userId)).willReturn(response);

		//When & Then
		mvc.perform(get("/api/carts"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.totalPrice").value(response.totalPrice()))
			.andExpect(jsonPath("$.data.deliveryFee").value(response.deliveryFee()))
			.andExpect(jsonPath("$.data.cartProducts").isArray())
			.andExpect(jsonPath("$.data.cartProducts[0].productId").value(cartProductResponse1.productId()))
			.andExpect(jsonPath("$.data.cartProducts[0].productName").value(cartProductResponse1.productName()))
			.andExpect(jsonPath("$.data.cartProducts[0].quantity").value(cartProductResponse1.quantity()))
			.andDo(document(
				"user/cart/getCart",
				userApiDescription(TagDescription.CART, "장바구니 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					STATUS,
					fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
						.description(CART_TOTAL_PRICE.getDescription()),
					fieldWithPath("data.deliveryFee").type(JsonFieldType.NUMBER)
						.description(DELIVERY_FEE.getDescription()),
					fieldWithPath("data.cartProducts[].productId").type(JsonFieldType.NUMBER)
						.description(PRODUCT_ID.getDescription()),
					fieldWithPath("data.cartProducts[].productName").type(JsonFieldType.STRING)
						.description(PRODUCT_NAME.getDescription()),
					fieldWithPath("data.cartProducts[].productPrice").type(JsonFieldType.NUMBER)
						.description(PRODUCT_PRICE.getDescription()),
					fieldWithPath("data.cartProducts[].quantity").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data.cartProducts[].totalPrice").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_TOTAL_PRICE.getDescription())
				)
			));
		then(cartProductService).should().findCartProducts(userId);
	}

	@WithUserDetails
	@DisplayName("[API][POST] 장바구니에 상품 추가")
	@Test
	void test_addProductToCart() throws Exception {
		//Given
		long productId = 1L;
		int quantity = 2;
		CartProductServiceRequest.Create serviceRequest =
			new CartProductRequest.Create(productId, quantity).toServiceRequest();
		CartProductResponse response = CartProductResponse.of(
			productId,
			"깐대파 500g",
			4500,
			quantity,
			9000
		);

		given(cartProductService.addCartProduct(userId, serviceRequest)).willReturn(response);

		//When & Then
		mvc.perform(
				post("/api/carts")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(new CartProductRequest.Create(productId, quantity)))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.productId").value(response.productId()))
			.andExpect(jsonPath("$.data.quantity").value(response.quantity()))
			.andDo(document(
				"user/cart/addProductToCart",
				userApiDescription(TagDescription.CART, "장바구니에 상품 추가"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("productId").type(JsonFieldType.NUMBER)
						.description(PRODUCT_ID.getDescription()),
					fieldWithPath("productQuantity").type(JsonFieldType.NUMBER)
						.description("장바구니에 담을 상품 수량")
				),
				responseFields(
					STATUS,
					fieldWithPath("data.productId").type(JsonFieldType.NUMBER)
						.description(PRODUCT_ID.getDescription()),
					fieldWithPath("data.productName").type(JsonFieldType.STRING)
						.description(PRODUCT_NAME.getDescription()),
					fieldWithPath("data.productPrice").type(JsonFieldType.NUMBER)
						.description(PRODUCT_PRICE.getDescription()),
					fieldWithPath("data.quantity").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_TOTAL_PRICE.getDescription())
				)
			));
		then(cartProductService).should().addCartProduct(userId, serviceRequest);
	}

	@WithUserDetails
	@DisplayName("[API][PUT] 장바구니 상품 수량 변경")
	@Test
	void test_updateProductInCart() throws Exception {
		//Given
		long cartProductId = 1L;
		int updateQuantity = 5;
		CartProductRequest.Update request = new CartProductRequest.Update(updateQuantity);
		CartProductResponse response = CartProductResponse.of(
			1L,
			"깐대파 500g",
			4500,
			updateQuantity,
			4500 * updateQuantity
		);

		given(cartProductService.updateCartProductQuantity(userId, cartProductId, request.productQuantity()))
			.willReturn(response);

		//When & Then
		mvc.perform(
				put("/api/carts/products/{cartProductId}", cartProductId)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.productId").value(response.productId()))
			.andExpect(jsonPath("$.data.quantity").value(response.quantity()))
			.andDo(document(
				"user/cart/updateProductInCart",
				userApiDescription(TagDescription.CART, "장바구니 상품 수량 변경"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("cartProductId").description(CART_PRODUCT_ID.getDescription())
				),
				requestFields(
					fieldWithPath("productQuantity").type(JsonFieldType.NUMBER).description("상품 수량")
				),
				responseFields(
					STATUS,
					fieldWithPath("data.productId").type(JsonFieldType.NUMBER)
						.description(PRODUCT_ID.getDescription()),
					fieldWithPath("data.productName").type(JsonFieldType.STRING)
						.description(PRODUCT_NAME.getDescription()),
					fieldWithPath("data.productPrice").type(JsonFieldType.NUMBER)
						.description(PRODUCT_PRICE.getDescription()),
					fieldWithPath("data.quantity").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_TOTAL_PRICE.getDescription())
				)
			));
		then(cartProductService).should().updateCartProductQuantity(userId, cartProductId, updateQuantity);
	}

	@WithUserDetails
	@DisplayName("[API][DELETE] 장바구니 상품 (복수) 삭제")
	@Test
	void test_deleteCartProducts() throws Exception {
		//Given
		long cartProductId1 = 1L;
		long cartProductId2 = 2L;
		List<Long> cartProductIds = List.of(cartProductId1, cartProductId2);
		willDoNothing().given(cartProductService).deleteCartProducts(userId, cartProductIds);

		//When & Then
		mvc.perform(
				delete("/api/carts/products")
					.param("ids", String.valueOf(cartProductId1), String.valueOf(cartProductId2))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andDo(document(
				"user/cart/deleteCartProducts",
				userApiDescription(TagDescription.CART, "장바구니 상품 (복수) 삭제"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(parameterWithName("ids").description("삭제할 장바구니 상품 시퀀스")),
				responseFields(STATUS)
			));
		then(cartProductService).should().deleteCartProducts(userId, cartProductIds);
	}
}
