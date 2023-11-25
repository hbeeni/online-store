package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.CartProductRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.service.CartProductService;
import com.been.onlinestore.service.CartService;
import com.been.onlinestore.service.response.CartIdAndCartProductIdResponse;
import com.been.onlinestore.service.response.CartProductResponse;
import com.been.onlinestore.service.response.CartWithCartProductsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.been.onlinestore.controller.restdocs.FieldDescription.ADD;
import static com.been.onlinestore.controller.restdocs.FieldDescription.CART_ID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.CART_PRODUCT_ID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.CART_PRODUCT_QUANTITY;
import static com.been.onlinestore.controller.restdocs.FieldDescription.CART_PRODUCT_TOTAL_PRICE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.CART_TOTAL_PRICE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_DELIVERY_FEE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_ID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_NAME;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_PRICE;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.STATUS;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.userApiDescription;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 장바구니")
@Import(TestSecurityConfig.class)
@WebMvcTest(CartApiController.class)
class CartApiControllerTest extends RestDocsSupport {

    private static final Long USER_ID = TestSecurityConfig.USER_ID;

    @MockBean private CartService cartService;
    @MockBean private CartProductService cartProductService;

    @WithUserDetails
    @DisplayName("[API][GET] 장바구니 상품 리스트 조회")
    @Test
    void test_getProductsInCart() throws Exception {
        //Given
        CartProductResponse cartProductResponse1 = CartProductResponse.of(
                1L,
                1L,
                "꽃무늬 바지",
                10000,
                2,
                20000,
                3000
        );
        CartProductResponse cartProductResponse2 = CartProductResponse.of(
                2L,
                2L,
                "꽃무늬 셔츠",
                12000,
                1,
                12000,
                3000
        );
        CartWithCartProductsResponse response = CartWithCartProductsResponse.of(
                1L,
                cartProductResponse1.totalPrice(),
                List.of(cartProductResponse1, cartProductResponse2)
        );

        given(cartService.findCart(USER_ID)).willReturn(Optional.of(response));

        //When & Then
        mvc.perform(get("/api/carts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.cartId").value(response.cartId()))
                .andExpect(jsonPath("$.data.totalPrice").value(response.totalPrice()))
                .andExpect(jsonPath("$.data.cartProducts").isArray())
                .andExpect(jsonPath("$.data.cartProducts[0].productId").value(cartProductResponse1.productId()))
                .andExpect(jsonPath("$.data.cartProducts[0].productName").value(cartProductResponse1.productName()))
                .andExpect(jsonPath("$.data.cartProducts[0].productPrice").value(cartProductResponse1.productPrice()))
                .andExpect(jsonPath("$.data.cartProducts[0].quantity").value(cartProductResponse1.quantity()))
                .andDo(document(
                        "user/cart/getCart",
                        userApiDescription(TagDescription.CART, "장바구니 조회"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                STATUS,
                                fieldWithPath("data.cartId").type(JsonFieldType.NUMBER).description(CART_ID.getDescription()),
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description(CART_TOTAL_PRICE.getDescription()),
                                fieldWithPath("data.cartProducts[].cartProductId").type(JsonFieldType.NUMBER).description(CART_PRODUCT_ID.getDescription()),
                                fieldWithPath("data.cartProducts[].productId").type(JsonFieldType.NUMBER).description(PRODUCT_ID.getDescription()),
                                fieldWithPath("data.cartProducts[].productName").type(JsonFieldType.STRING).description(PRODUCT_NAME.getDescription()),
                                fieldWithPath("data.cartProducts[].productPrice").type(JsonFieldType.NUMBER).description(PRODUCT_PRICE.getDescription()),
                                fieldWithPath("data.cartProducts[].quantity").type(JsonFieldType.NUMBER).description(CART_PRODUCT_QUANTITY.getDescription()),
                                fieldWithPath("data.cartProducts[].totalPrice").type(JsonFieldType.NUMBER).description(CART_PRODUCT_TOTAL_PRICE.getDescription()),
                                fieldWithPath("data.cartProducts[].deliveryFee").type(JsonFieldType.NUMBER).description(PRODUCT_DELIVERY_FEE.getDescription())
                        )
                ));
        then(cartService).should().findCart(USER_ID);
    }

    @WithUserDetails
    @DisplayName("[API][POST] 장바구니에 상품 추가")
    @Test
    void test_addProductToCart() throws Exception {
        //Given
        long cartProductId = 1L;
        long cartId = 1L;
        CartProductRequest.Create request = CartProductRequest.Create.builder()
                .productId(1L)
                .productQuantity(2)
                .build();
        CartIdAndCartProductIdResponse response = CartIdAndCartProductIdResponse.of(cartId, cartProductId);

        given(cartService.addCartProductToCart(USER_ID, request.toServiceRequest())).willReturn(response);

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
                .andExpect(jsonPath("$.data.cartId").value(response.cartId()))
                .andExpect(jsonPath("$.data.cartProductId").value(response.cartProductId()))
                .andDo(document(
                        "user/cart/addProductToCart",
                        userApiDescription(TagDescription.CART, "장바구니에 상품 추가"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type(JsonFieldType.NUMBER).description(PRODUCT_ID.getDescription()),
                                fieldWithPath("productQuantity").type(JsonFieldType.NUMBER).description("장바구니에 담을 상품 수량")
                        ),
                        responseFields(
                                STATUS,
                                fieldWithPath("data.cartId").type(JsonFieldType.NUMBER).description(ADD.getDescription() + CART_PRODUCT_ID.getDescription()),
                                fieldWithPath("data.cartProductId").type(JsonFieldType.NUMBER).description(ADD.getDescription() + PRODUCT_ID.getDescription())
                        )
                ));
        then(cartService).should().addCartProductToCart(USER_ID, request.toServiceRequest());
    }

    @WithUserDetails
    @DisplayName("[API][PUT] 장바구니 상품 수량 변경")
    @Test
    void test_updateProductInCart() throws Exception {
        //Given
        long cartProductId = 1L;
        int productPrice = 12000;

        CartProductRequest.Update request = CartProductRequest.Update.builder()
                .productQuantity(5)
                .build();
        CartProductResponse response = CartProductResponse.of(
                cartProductId,
                1L,
                "꽃무늬 바지",
                productPrice,
                request.productQuantity(),
                productPrice * request.productQuantity(),
                3000
        );

        given(cartProductService.updateCartProductQuantity(cartProductId, USER_ID, request.productQuantity())).willReturn(response);

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
                .andExpect(jsonPath("$.data.cartProductId").value(response.cartProductId()))
                .andExpect(jsonPath("$.data.productId").value(response.productId()))
                .andExpect(jsonPath("$.data.productName").value(response.productName()))
                .andExpect(jsonPath("$.data.quantity").value(response.quantity()))
                .andExpect(jsonPath("$.data.deliveryFee").value(response.deliveryFee()))
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
                                fieldWithPath("data.cartProductId").type(JsonFieldType.NUMBER).description(CART_PRODUCT_ID.getDescription()),
                                fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description(PRODUCT_ID.getDescription()),
                                fieldWithPath("data.productName").type(JsonFieldType.STRING).description(PRODUCT_NAME.getDescription()),
                                fieldWithPath("data.productPrice").type(JsonFieldType.NUMBER).description(PRODUCT_PRICE.getDescription()),
                                fieldWithPath("data.quantity").type(JsonFieldType.NUMBER).description(CART_PRODUCT_QUANTITY.getDescription()),
                                fieldWithPath("data.totalPrice").type(JsonFieldType.NUMBER).description(CART_PRODUCT_TOTAL_PRICE.getDescription()),
                                fieldWithPath("data.deliveryFee").type(JsonFieldType.NUMBER).description(PRODUCT_DELIVERY_FEE.getDescription())
                        )
                ));
        then(cartProductService).should().updateCartProductQuantity(cartProductId, USER_ID, request.productQuantity());
    }

    @WithUserDetails
    @DisplayName("[API][DELETE] 장바구니 삭제")
    @Test
    void test_deleteCart() throws Exception {
        //Given
        willDoNothing().given(cartService).deleteCart(USER_ID);

        //When & Then
        mvc.perform(delete("/api/carts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andDo(document(
                        "user/cart/deleteCart",
                        userApiDescription(TagDescription.CART, "장바구니 삭제 (장바구니 상품 전체 삭제)"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                STATUS
                        )
                ));
        then(cartService).should().deleteCart(USER_ID);
    }

    @WithUserDetails
    @DisplayName("[API][DELETE] 장바구니 상품 (복수) 삭제")
    @Test
    void test_deleteCartProducts() throws Exception {
        //Given
        Set<Long> cartProductIds = Set.of(1L, 2L, 3L);

        willDoNothing().given(cartProductService).deleteCartProducts(cartProductIds, USER_ID);

        //When & Then
        mvc.perform(
                        delete("/api/carts/products")
                                .queryParam("ids", "1", "2", "3")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andDo(document(
                        "user/cart/deleteCartProducts",
                        userApiDescription(TagDescription.CART, "장바구니 상품 선택 삭제"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("ids").description("삭제할 상품 시퀀스 (복수)")
                        ),
                        responseFields(
                                STATUS
                        )
                ));
        then(cartProductService).should().deleteCartProducts(cartProductIds, USER_ID);
    }
}
