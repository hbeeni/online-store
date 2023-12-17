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
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.CartProductRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.dto.response.CartResponse;
import com.been.onlinestore.service.dto.response.CartResponse.CartProductResponse;

@DisplayName("API 컨트롤러 - 장바구니")
@Import(TestSecurityConfig.class)
@WebMvcTest(CartApiController.class)
class CartApiControllerTest extends RestDocsSupport {

	private static final String COOKIE_NAME = "cart";

	@MockBean
	private ProductService productService;

	@DisplayName("[API][GET] 장바구니 상품 리스트 조회")
	@Test
	void test_getProductsInCart() throws Exception {
		//Given
		CartResponse cartResponse1 = CartResponse.of(
			1L,
			"꽃무늬 바지",
			10000,
			2,
			20000,
			3000
		);
		CartResponse cartResponse2 = CartResponse.of(
			2L,
			"꽃무늬 셔츠",
			12000,
			1,
			12000,
			3000
		);

		Map<Long, Integer> idToQuantityMap = Map.of(1L, 2, 2L, 1);
		Cookie cookie = new Cookie(COOKIE_NAME, "1:2|2:1");

		given(productService.findProductsInCart(idToQuantityMap)).willReturn(List.of(cartResponse1, cartResponse2));

		//When & Then
		mvc.perform(get("/api/carts").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].productId").value(cartResponse1.productId()))
			.andExpect(jsonPath("$.data[0].quantity").value(cartResponse1.quantity()))
			.andDo(document(
				"user/cart/getCart",
				userApiDescription(TagDescription.CART, "장바구니 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					STATUS,
					fieldWithPath("data[].productId").type(JsonFieldType.NUMBER)
						.description(PRODUCT_ID.getDescription()),
					fieldWithPath("data[].productName").type(JsonFieldType.STRING)
						.description(PRODUCT_NAME.getDescription()),
					fieldWithPath("data[].productPrice").type(JsonFieldType.NUMBER)
						.description(PRODUCT_PRICE.getDescription()),
					fieldWithPath("data[].quantity").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_QUANTITY.getDescription()),
					fieldWithPath("data[].totalProductPrice").type(JsonFieldType.NUMBER)
						.description(CART_PRODUCT_TOTAL_PRICE.getDescription()),
					fieldWithPath("data[].deliveryFee").type(JsonFieldType.NUMBER)
						.description(PRODUCT_DELIVERY_FEE.getDescription())
				)
			));
		then(productService).should().findProductsInCart(idToQuantityMap);
	}

	@DisplayName("[API][POST] 장바구니에 상품 추가")
	@Test
	void test_addProductToCart() throws Exception {
		//Given
		CartProductRequest.Create request = CartProductRequest.Create.builder()
			.productId(1L)
			.productQuantity(2)
			.build();

		//When & Then
		mvc.perform(
				post("/api/carts")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(cookie().value(COOKIE_NAME, "1:2"))
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
				responseFields(STATUS)
			));
	}

	@DisplayName("[API][PUT] 장바구니 상품 수량 변경")
	@Test
	void test_updateProductInCart() throws Exception {
		//Given
		long cartProductId = 1L;

		CartProductRequest.Update request = CartProductRequest.Update.builder()
			.productQuantity(5)
			.build();

		Cookie cookie = new Cookie(COOKIE_NAME, "1:2|2:1");

		//When & Then
		mvc.perform(
				put("/api/carts/products/{cartProductId}", cartProductId)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
					.cookie(cookie)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(cookie().value(COOKIE_NAME, "1:5|2:1"))
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
				responseFields(STATUS)
			));
	}

	@DisplayName("[API][DELETE] 장바구니 삭제")
	@Test
	void test_deleteCart() throws Exception {
		//Given
		Cookie cookie = new Cookie(COOKIE_NAME, "1:2|2:1");

		//When & Then
		mvc.perform(delete("/api/carts").cookie(cookie))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(cookie().maxAge(COOKIE_NAME, 0))
			.andDo(document(
				"user/cart/deleteCart",
				userApiDescription(TagDescription.CART, "장바구니 삭제 (장바구니 상품 전체 삭제)"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(STATUS)
			));
	}

	@DisplayName("[API][DELETE] 장바구니 상품 (복수) 삭제")
	@Test
	void test_deleteCartProducts() throws Exception {
		//Given
		Cookie cookie = new Cookie(COOKIE_NAME, "1:2|2:1|3:5|4:1");

		//When & Then
		mvc.perform(
				delete("/api/carts/products")
					.queryParam("ids", "1", "2", "3")
					.cookie(cookie)
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(cookie().value(COOKIE_NAME, "4:1"))
			.andDo(document(
				"user/cart/deleteCartProducts",
				userApiDescription(TagDescription.CART, "장바구니 상품 선택 삭제"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(
					parameterWithName("ids").description("삭제할 상품 시퀀스 (복수)")
				),
				responseFields(STATUS)
			));
	}
}
