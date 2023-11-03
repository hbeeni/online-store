package com.been.onlinestore.controller.user;

import com.been.onlinestore.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 장바구니 (일반)")
@Import(SecurityConfig.class)
@WebMvcTest(CartApiController.class)
class CartApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private static final long CART_ID = 1L;
    private static final long PRODUCT_ID = 1L;
    private static final String PRODUCT_NAME = "test product";
    private static final int PRODUCT_PRICE = 10000;
    private static final int PRODUCT_QUANTITY = 5;

    @DisplayName("[API][GET] 장바구니 상품 리스트 조회")
    @Test
    void test_getProductsInCart() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/user/carts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.cartId").value(CART_ID))
                .andExpect(jsonPath("$.data.totalPrice").value(PRODUCT_PRICE * PRODUCT_QUANTITY))
                .andExpect(jsonPath("$.data.cartProducts[0].productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.data.cartProducts[0].productName").value(PRODUCT_NAME))
                .andExpect(jsonPath("$.data.cartProducts[0].productPrice").value(PRODUCT_PRICE))
                .andExpect(jsonPath("$.data.cartProducts[0].productQuantity").value(PRODUCT_QUANTITY));
    }

    @DisplayName("[API][GET] 장바구니 상품 리스트 조회 + 페이징")
    @Test
    void test_getProductsInCart_withPagination() throws Exception {
        //Given
        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 1;

        //When & Then
        mvc.perform(
                        get("/api/user/carts")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.cartId").value(CART_ID))
                .andExpect(jsonPath("$.data.totalPrice").value(PRODUCT_PRICE * PRODUCT_QUANTITY))
                .andExpect(jsonPath("$.data.cartProducts[0].productId").value(PRODUCT_ID))
                .andExpect(jsonPath("$.data.cartProducts[0].productName").value(PRODUCT_NAME))
                .andExpect(jsonPath("$.data.cartProducts[0].productPrice").value(PRODUCT_PRICE))
                .andExpect(jsonPath("$.data.cartProducts[0].productQuantity").value(PRODUCT_QUANTITY))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @DisplayName("[API][POST] 장바구니에 상품 추가")
    @Test
    void test_addProductToCart() throws Exception {
        //Given
        long cartProductId = 1L;

        //When & Then
        mvc.perform(post("/api/user/carts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.cartId").value(CART_ID))
                .andExpect(jsonPath("$.data.cartProductId").value(cartProductId));
    }

    @DisplayName("[API][PUT] 장바구니에 있는 상품 수정")
    @Test
    void test_updateProductInCart() throws Exception {
        //Given
        long cartProductId = 1L;
        int productQuantity = 100;

        //When & Then
        mvc.perform(put("/api/user/carts/products/" + cartProductId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(cartProductId));
    }

    @DisplayName("[API][DELETE] 장바구니 삭제")
    @Test
    void test_deleteCart() throws Exception {
        //Given

        //When & Then
        mvc.perform(delete("/api/user/carts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(CART_ID));
    }
}
