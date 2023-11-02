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
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.name").value(NAME))
                .andExpect(jsonPath("$.data.productCount").value(PRODUCT_COUNT));
    }
}
