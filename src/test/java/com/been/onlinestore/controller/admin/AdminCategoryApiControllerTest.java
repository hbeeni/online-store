package com.been.onlinestore.controller.admin;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.been.onlinestore.util.CategoryTestDataUtil.createCategoryDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 카테고리 (관리자)")
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(AdminCategoryApiController.class)
class AdminCategoryApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private CategoryService categoryService;

    private static final long ID = 1L;

    @DisplayName("[API][GET] 카테고리 상세 조회")
    @Test
    void test_getCategory() throws Exception {
        //Given
        String name = "test category";
        int productCount = 1;
        given(categoryService.findCategory(any()))
                .willReturn(createCategoryDto(ID, name, productCount));

        //When & Then
        mvc.perform(get("/api/admin/categories/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.productCount").value(productCount));
        then(categoryService).should().findCategory(any());
    }

    @DisplayName("[API][POST] 카테고리 추가")
    @Test
    void test_addCategory() throws Exception {
        //Given
        given(categoryService.addCategory(any())).willReturn(ID);

        //When & Then
        mvc.perform(
                        post("/api/admin/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content("""
                                {
                                    "name": "category",
                                    "description": "description"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
        then(categoryService).should().addCategory(any());
    }

    @DisplayName("[API][PUT] 카테고리 수정")
    @Test
    void test_updateCategory() throws Exception {
        //Given
        String name = "updated name";
        given(categoryService.updateCategory(any(), any())).willReturn(ID);

        //When & Then
        mvc.perform(
                        put("/api/admin/categories/" + ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content("""
                                        {
                                            "name": "updated name",
                                            "description": "updated name description"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
        then(categoryService).should().updateCategory(any(), any());
    }

    @DisplayName("[API][DELETE] 카테고리 삭제")
    @Test
    void test_deleteCategory() throws Exception {
        //Given
        given(categoryService.deleteCategory(any())).willReturn(ID);

        //When & Then
        mvc.perform(delete("/api/admin/categories/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
        then(categoryService).should().deleteCategory(any());
    }
}
