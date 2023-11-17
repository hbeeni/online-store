package com.been.onlinestore.controller.admin;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.CategoryRequest;
import com.been.onlinestore.service.CategoryService;
import com.been.onlinestore.service.response.admin.AdminCategoryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.been.onlinestore.util.CategoryTestDataUtil.createAdminCategoryResponse;
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

@DisplayName("API 컨트롤러 - 카테고리 (관리자)")
@Import(TestSecurityConfig.class)
@WebMvcTest(AdminCategoryApiController.class)
class AdminCategoryApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private CategoryService categoryService;

    @DisplayName("[API][GET] 카테고리 조회")
    @Test
    void test_getCategories() throws Exception {
        //Given
        long id = 1L;
        AdminCategoryResponse response = createAdminCategoryResponse(id, "test category", 1);

        given(categoryService.findCategoriesForAdmin()).willReturn(List.of(response));

        //When & Then
        mvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(response.id()))
                .andExpect(jsonPath("$.data[0].name").value(response.name()))
                .andExpect(jsonPath("$.data[0].productCount").value(response.productCount()))
                .andExpect(jsonPath("$.data[0].createdBy").value(response.createdBy()))
                .andExpect(jsonPath("$.data[0].modifiedBy").value(response.modifiedBy()));
        then(categoryService).should().findCategoriesForAdmin();
    }

    @DisplayName("[API][GET] 카테고리 상세 조회")
    @Test
    void test_getCategory() throws Exception {
        //Given
        long id = 1L;
        AdminCategoryResponse response = createAdminCategoryResponse(id, "test category", 1);

        given(categoryService.findCategory(id)).willReturn(response);

        //When & Then
        mvc.perform(get("/api/admin/categories/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(response.id()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.productCount").value(response.productCount()))
                .andExpect(jsonPath("$.data.createdBy").value(response.createdBy()))
                .andExpect(jsonPath("$.data.modifiedBy").value(response.modifiedBy()));
        then(categoryService).should().findCategory(id);
    }

    @DisplayName("[API][POST] 카테고리 추가")
    @Test
    void test_addCategory() throws Exception {
        //Given
        long id = 1L;
        CategoryRequest.Create request = CategoryRequest.Create.builder()
                .name("상의")
                .description("상의입니다.")
                .build();
        given(categoryService.addCategory(request.toServiceRequest())).willReturn(id);

        //When & Then
        mvc.perform(
                        post("/api/admin/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(id));
        then(categoryService).should().addCategory(request.toServiceRequest());
    }

    @DisplayName("[API][PUT] 카테고리 수정")
    @Test
    void test_updateCategory() throws Exception {
        //Given
        long id = 1L;
        String name = "updated name";
        CategoryRequest.Update request = CategoryRequest.Update.builder()
                .name(name)
                .build();
        given(categoryService.updateCategory(id, request.toServiceRequest())).willReturn(id);

        //When & Then
        mvc.perform(
                        put("/api/admin/categories/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(id));
        then(categoryService).should().updateCategory(id, request.toServiceRequest());
    }

    @DisplayName("[API][DELETE] 카테고리 삭제")
    @Test
    void test_deleteCategory() throws Exception {
        //Given
        long id = 1L;
        given(categoryService.deleteCategory(id)).willReturn(id);

        //When & Then
        mvc.perform(delete("/api/admin/categories/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(id));
        then(categoryService).should().deleteCategory(any());
    }
}
