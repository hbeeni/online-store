package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.response.CategoryProductResponse;
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

import static com.been.onlinestore.util.ProductTestDataUtil.createCategoryProductResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 상품")
@Import(TestSecurityConfig.class)
@WebMvcTest(ProductApiController.class)
class ProductApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean private ProductService productService;

    @DisplayName("[API][GET] 신상품 조회 - 기본: 생성일 내림차순으로 20개")
    @Test
    void test_get20ProductsOrderByCreatedAtDesc() throws Exception {
        //Given
        long productId = 1L;
        String productName = "test product";
        int productPrice = 10000;

        int pageNumber = 0;
        int pageSize = 20;
        String sortName = "createdAt";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        Page<CategoryProductResponse> page = new PageImpl<>(List.of(createCategoryProductResponse(productId, productName, "category")), pageable, 1);

        given(productService.findProductsOnSaleForUser(null, pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                    get("/api/products")
                            .queryParam("page", String.valueOf(pageNumber))
                            .queryParam("size", String.valueOf(pageSize))
                            .queryParam("sort", sortName + ",desc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(productId))
                .andExpect(jsonPath("$.data[0].name").value(productName))
                .andExpect(jsonPath("$.data[0].price").value(productPrice))
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
        then(productService).should().findProductsOnSaleForUser(null, pageable);
    }

    @DisplayName("[API][GET] 상품 조회 - 생성일 내림차순으로 20개 + 상품명 검색")
    @Test
    void test_getProducts_whenSearchingProductName() throws Exception {
        //Given
        long productId = 1L;
        String productName = "test product";
        int productPrice = 10000;

        int pageNumber = 0;
        int pageSize = 20;
        String sortName = "createdAt";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        Page<CategoryProductResponse> page = new PageImpl<>(List.of(createCategoryProductResponse(productId, productName, "category")), pageable, 1);

        given(productService.findProductsOnSaleForUser(productName, pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                    get("/api/products")
                            .queryParam("page", String.valueOf(pageNumber))
                            .queryParam("size", String.valueOf(pageSize))
                            .queryParam("sort", sortName + ",desc")
                            .queryParam("name", productName)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(productId))
                .andExpect(jsonPath("$.data[0].name").value(productName))
                .andExpect(jsonPath("$.data[0].price").value(productPrice))
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
        then(productService).should().findProductsOnSaleForUser(productName, pageable);
    }

    @DisplayName("[API][GET] 상품 상세 조회")
    @Test
    void test_getProduct() throws Exception {
        //Given
        long productId = 1L;
        String productName = "test product";
        int productPrice = 10000;

        given(productService.findProductOnSaleForUser(productId)).willReturn(createCategoryProductResponse(productId, productName, "category"));

        //When & Then
        mvc.perform(get("/api/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.name").value(productName))
                .andExpect(jsonPath("$.data.price").value(productPrice));
        then(productService).should().findProductOnSaleForUser(productId);
    }
}
