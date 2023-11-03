package com.been.onlinestore.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 공통")
@Import(SecurityConfig.class)
@WebMvcTest(CommonController.class)
class CommonControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @DisplayName("[API][GET] 카테고리 리스트 조회")
    @Test
    void test_getAllCategories() throws Exception {
        //Given
        long categoryId = 1L;
        String categoryName = "test category";
        int productCountInCategory = 1;

        //When & Then
        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(categoryId))
                .andExpect(jsonPath("$.data[0].name").value(categoryName))
                .andExpect(jsonPath("$.data[0].productCount").value(productCountInCategory));
    }

    @DisplayName("[API][GET] 해당 카테고리의 상품 리스트 조회")
    @Test
    void test_getAllProductsInCategory() throws Exception {
        //Given
        long categoryId = 1L;
        String categoryName = "test category";

        long productId = 1L;
        String productName = "test product";

        //When & Then
        mvc.perform(get("/api/categories/" + categoryId + "/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.categoryName").value(categoryName))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products[0].id").value(productId))
                .andExpect(jsonPath("$.data.products[0].name").value(productName));
    }

    @DisplayName("[API][GET] 해당 카테고리의 상품 리스트 조회 + 페이징")
    @Test
    void test_getAllProductsInCategory_withPagination() throws Exception {
        //Given
        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 1;

        long categoryId = 1L;
        String categoryName = "test category";
        long productId = 1L;
        String productName = "test product";

        //When & Then
        mvc.perform(
                        get("/api/categories/" + categoryId + "/products")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.categoryName").value(categoryName))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products[0].id").value(productId))
                .andExpect(jsonPath("$.data.products[0].name").value(productName))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

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
