package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.CartProductRequest;
import com.been.onlinestore.service.CartProductService;
import com.been.onlinestore.service.CartService;
import com.been.onlinestore.service.response.CartIdAndCartProductIdResponse;
import com.been.onlinestore.service.response.CartProductResponse;
import com.been.onlinestore.service.response.CartWithCartProductsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 장바구니")
@Import(TestSecurityConfig.class)
@WebMvcTest(CartApiController.class)
class CartApiControllerTest {

    private static final Long USER_ID = TestSecurityConfig.USER_ID;

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private CartService cartService;
    @MockBean private CartProductService cartProductService;

    @WithUserDetails
    @DisplayName("[API][GET] 장바구니 상품 리스트 조회")
    @Test
    void test_getProductsInCart() throws Exception {
        //Given
        CartProductResponse cartProductResponse = CartProductResponse.of(
                1L,
                1L,
                "test product",
                10000,
                5,
                10000 * 5,
                3000
        );
        CartWithCartProductsResponse response = CartWithCartProductsResponse.of(
                1L,
                cartProductResponse.totalPrice(),
                List.of(cartProductResponse)
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
                .andExpect(jsonPath("$.data.cartProducts[0].productId").value(cartProductResponse.productId()))
                .andExpect(jsonPath("$.data.cartProducts[0].productName").value(cartProductResponse.productName()))
                .andExpect(jsonPath("$.data.cartProducts[0].productPrice").value(cartProductResponse.productPrice()))
                .andExpect(jsonPath("$.data.cartProducts[0].quantity").value(cartProductResponse.quantity()));
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
                .productQuantity(5)
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
                .andExpect(jsonPath("$.data.cartProductId").value(response.cartProductId()));
        then(cartService).should().addCartProductToCart(USER_ID, request.toServiceRequest());
    }

    @WithUserDetails
    @DisplayName("[API][PUT] 장바구니 상품 수량 변경")
    @Test
    void test_updateProductInCart() throws Exception {
        //Given
        long cartProductId = 1L;

        CartProductRequest.Update request = CartProductRequest.Update.builder()
                .productQuantity(10)
                .build();
        CartProductResponse response = CartProductResponse.of(cartProductId, 1L, "name", 10000, request.productQuantity(), 30000, 3000);

        given(cartProductService.updateCartProductQuantity(cartProductId, USER_ID, request.productQuantity())).willReturn(response);

        //When & Then
        mvc.perform(
                        put("/api/carts/products/" + cartProductId)
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
                .andExpect(jsonPath("$.data.deliveryFee").value(response.deliveryFee()));
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
                .andExpect(jsonPath("$.status").value("success"));
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
                .andExpect(jsonPath("$.status").value("success"));
        then(cartProductService).should().deleteCartProducts(cartProductIds, USER_ID);
    }
}
