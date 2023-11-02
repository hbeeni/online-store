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
@DisplayName("API 컨트롤러 - 카테고리 (일반)")
@Import(SecurityConfig.class)
@WebMvcTest(CategoryApiController.class)
class CategoryApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private static final long ID = 1L;
    private static final String NAME = "test category";
    private static final int PRODUCT_COUNT = 1;

    @DisplayName("[API][GET] 카테고리 리스트 조회")
    @Test
    void test_getAllCategories() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(ID))
                .andExpect(jsonPath("$.data[0].name").value(NAME))
                .andExpect(jsonPath("$.data[0].productCount").value(PRODUCT_COUNT));
    }

    @DisplayName("[API][GET] 카테고리 상세 조회")
    @Test
    void test_getCategory() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/categories/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.name").value(NAME))
                .andExpect(jsonPath("$.data.productCount").value(PRODUCT_COUNT));
    }

    @DisplayName("[API][GET] 해당 카테고리의 상품 리스트 조회")
    @Test
    void test_getAllProductsInCategory() throws Exception {
        //Given
        long productId = 1L;
        String productName = "test product";

        //When & Then
        mvc.perform(get("/api/categories/" + ID + "/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.categoryName").value(NAME))
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

        long productId = 1L;
        String productName = "test product";

        //When & Then
        mvc.perform(
                        get("/api/categories/" + ID + "/products")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.categoryName").value(NAME))
                .andExpect(jsonPath("$.data.products").isArray())
                .andExpect(jsonPath("$.data.products[0].id").value(productId))
                .andExpect(jsonPath("$.data.products[0].name").value(productName))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }
}
