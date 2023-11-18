package com.been.onlinestore.controller.admin;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.ProductService;
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

import static com.been.onlinestore.util.ProductTestDataUtil.createAdminProductResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 상품 (관리자)")
@Import(TestSecurityConfig.class)
@WebMvcTest(AdminProductApiController.class)
class AdminProductApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private ProductService productService;

    @DisplayName("[API][GET] 상품 리스트 조회 + 페이징")
    @Test
    void test_getProducts_withPagination() throws Exception {
        //Given
        long id = 1L;
        String name = "test product";

        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;

        ProductSearchCondition cond = ProductSearchCondition.of(null, null, null);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        AdminProductResponse response = createAdminProductResponse(id, name, "category");
        Page<AdminProductResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        given(productService.findProductsForAdmin(cond, pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                        get("/api/admin/products")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(response.id()))
                .andExpect(jsonPath("$.data[0].name").value(response.name()))
                .andExpect(jsonPath("$.data[0].price").value(response.price()))
                .andExpect(jsonPath("$.data[0].seller").exists())
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
        then(productService).should().findProductsForAdmin(cond, pageable);
    }

    @DisplayName("[API][GET] 상품 리스트 조회 + 검색 + 페이징")
    @Test
    void test_getProducts_whenSearching_withPagination() throws Exception {
        //Given
        long id = 1L;
        String name = "test product";

        String searchName = "test";
        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;

        ProductSearchCondition cond = ProductSearchCondition.of(null, searchName, null);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        AdminProductResponse response = createAdminProductResponse(id, name, "category");
        Page<AdminProductResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        given(productService.findProductsForAdmin(cond, pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                        get("/api/admin/products")
                                .queryParam("name", searchName)
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(response.id()))
                .andExpect(jsonPath("$.data[0].name").value(response.name()))
                .andExpect(jsonPath("$.data[0].price").value(response.price()))
                .andExpect(jsonPath("$.data[0].seller").exists())
                .andExpect(jsonPath("$.data[0].createdAt").exists())
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
        then(productService).should().findProductsForAdmin(cond, pageable);
    }

    @DisplayName("[API][GET] 상품 상세 조회")
    @Test
    void test_getProduct() throws Exception {
        //Given
        long id = 1L;
        String name = "test product";

        AdminProductResponse response = createAdminProductResponse(id, name, "category");
        given(productService.findProductForAdmin(id)).willReturn(response);

        //When & Then
        mvc.perform(get("/api/admin/products/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(response.id()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.price").value(response.price()));
        then(productService).should().findProductForAdmin(id);
    }
}
