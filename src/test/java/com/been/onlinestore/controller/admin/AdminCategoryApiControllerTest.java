package com.been.onlinestore.controller.admin;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 카테고리 (관리자)")
@Import(SecurityConfig.class)
@WebMvcTest(AdminCategoryApiController.class)
class AdminCategoryApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private static final long ID = 1L;

    @DisplayName("[API][POST] 카테고리 추가")
    @Test
    void test_addCategory() throws Exception {
        //Given

        //When & Then
        mvc.perform(post("/admin-api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
    }

    @DisplayName("[API][PUT] 카테고리 수정")
    @Test
    void test_updateCategory() throws Exception {
        //Given
        String name = "updated name";
        String description = "updated description";

        //When & Then
        mvc.perform(delete("/admin-api/categories/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
    }

    @DisplayName("[API][DELETE] 카테고리 삭제")
    @Test
    void test_deleteCategory() throws Exception {
        //Given

        //When & Then
        mvc.perform(delete("/admin-api/categories/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
    }
}
