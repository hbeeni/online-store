package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 권한 필요 없음")
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(HomeApiController.class)
class HomeApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @Disabled("구현 전")
    @DisplayName("[API][GET] 신상품 조회 - 생성일 내림차순으로 9개")
    @Test
    void test_get9ProductsOrderByCreatedAtDesc() throws Exception {
        //Given
        long productId = 1L;
        String productName = "test category";
        int productPrice = 10000;
        //상품 10개 생성

        //When & Then
        mvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(9))
                .andExpect(jsonPath("$.data[0].id").value(productId))
                .andExpect(jsonPath("$.data[0].name").value(productName))
                .andExpect(jsonPath("$.data[0].price").value(productPrice));
    }

    @Disabled("구현 전")
    @DisplayName("[API][GET] 상품 상세 조회")
    @Test
    void test_getProduct() throws Exception {
        //Given
        long productId = 1L;
        String productName = "test category";
        int productPrice = 10000;

        //When & Then
        mvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.name").value(productName))
                .andExpect(jsonPath("$.data.price").value(productPrice));
    }
}
