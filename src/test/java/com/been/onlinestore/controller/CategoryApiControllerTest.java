package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.service.CategoryService;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.been.onlinestore.util.CategoryTestDataUtil.createCategoryResponse;
import static com.been.onlinestore.util.ProductTestDataUtil.createProductResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 카테고리")
@Import(TestSecurityConfig.class)
@WebMvcTest(CategoryApiController.class)
class CategoryApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private CategoryService categoryService;
    @MockBean private ProductService productService;

    @DisplayName("[API][GET] 카테고리 리스트 조회")
    @Test
    void test_getAllCategories() throws Exception {
        //Given
        long categoryId = 1L;
        String categoryName = "test category";
        int productCountInCategory = 1;
        given(categoryService.findCategoriesForUser()).willReturn(List.of(createCategoryResponse(categoryId, categoryName, productCountInCategory)));

        //When & Then
        mvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(categoryId))
                .andExpect(jsonPath("$.data[0].name").value(categoryName))
                .andExpect(jsonPath("$.data[0].productCount").value(productCountInCategory));
        then(categoryService).should().findCategoriesForUser();
    }

    @DisplayName("[API][GET] 해당 카테고리의 상품 리스트 조회 + 페이징")
    @Test
    void test_getAllProductsInCategory_withPagination() throws Exception {
        //Given
        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));

        long categoryId = 1L;
        ProductResponse productResponse = createProductResponse(1L);
        Page<ProductResponse> page = new PageImpl<>(List.of(productResponse), pageable, 1);

        given(productService.findProductsInCategoryForUser(categoryId, pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                        get("/api/categories/" + categoryId)
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(productResponse.id()))
                .andExpect(jsonPath("$.data[0].name").value(productResponse.name()))
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
    }
}
