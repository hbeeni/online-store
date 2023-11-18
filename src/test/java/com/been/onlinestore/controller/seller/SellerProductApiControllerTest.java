package com.been.onlinestore.controller.seller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.ProductRequest;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.been.onlinestore.util.ProductTestDataUtil.createAdminProductResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 상품 (판매자)")
@Import(TestSecurityConfig.class)
@WebMvcTest(SellerProductApiController.class)
class SellerProductApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private ProductService productService;

    @WithUserDetails
    @DisplayName("[API][GET] 상품 리스트 조회 + 페이징")
    @Test
    void test_getProductList_withPagination() throws Exception {
        //Given
        long productId = 1L;
        String name = "test product";

        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;

        ProductSearchCondition cond = ProductSearchCondition.of(null, null, null);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        AdminProductResponse response = createAdminProductResponse(productId, name, "category");
        Page<AdminProductResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        given(productService.findProductsForSeller(any(), eq(cond), eq(pageable))).willReturn(page);

        //When & Then
        mvc.perform(
                        get("/api/seller/products")
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
        then(productService).should().findProductsForSeller(any(), eq(cond), eq(pageable));
    }

    @WithUserDetails
    @DisplayName("[API][GET] 상품 리스트 조회 + 검색 + 페이징")
    @Test
    void test_getProductList_whenSearching_withPagination() throws Exception {
        //Given
        long productId = 1L;
        String name = "test product";

        String searchName = "test";
        String sortName = "createdAt";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 10;

        ProductSearchCondition cond = ProductSearchCondition.of(null, searchName, null);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        AdminProductResponse response = createAdminProductResponse(productId, name, "category");
        Page<AdminProductResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        given(productService.findProductsForSeller(any(), eq(cond), eq(pageable))).willReturn(page);

        //When & Then
        mvc.perform(
                        get("/api/seller/products")
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
        then(productService).should().findProductsForSeller(any(), eq(cond), eq(pageable));
    }

    @WithUserDetails
    @DisplayName("[API][GET] 상품 상세 조회")
    @Test
    void test_getProduct() throws Exception {
        //Given
        long productId = 1L;
        String name = "test product";
        AdminProductResponse response = createAdminProductResponse(productId, name, "category");

        given(productService.findProductForSeller(eq(productId), any())).willReturn(response);

        //When & Then
        mvc.perform(get("/api/seller/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(response.id()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.price").value(response.price()));
        then(productService).should().findProductForSeller(eq(productId), any());
    }

    @WithUserDetails
    @DisplayName("[API][POST] 상품 등록")
    @Test
    void test_addProduct() throws Exception {
        //Given
        long productId = 1L;
        ProductRequest.Create request = ProductRequest.Create.builder()
                .categoryId(1L)
                .name("name")
                .price(10000)
                .stockQuantity(1000)
                .saleStatus(SaleStatus.SALE)
                .deliveryFee(2500)
                .build();
        given(productService.addProduct(any(), eq(request.toServiceRequest()))).willReturn(productId);

        //When & Then
        mvc.perform(
                        post("/api/seller/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(productId));
        then(productService).should().addProduct(any(), eq(request.toServiceRequest()));
    }

    @WithUserDetails
    @DisplayName("[API][PUT] 상품 수정")
    @Test
    void test_updateProduct() throws Exception {
        //Given
        long productId = 1L;
        ProductRequest.Update request = ProductRequest.Update.builder()
                .categoryId(1L)
                .name("name")
                .price(10000)
                .stockQuantity(1000)
                .saleStatus(SaleStatus.SALE)
                .deliveryFee(2500)
                .build();

        given(productService.updateProductInfo(eq(productId), any(), eq(request.toServiceRequest()))).willReturn(productId);

        //When & Then
        mvc.perform(
                    put("/api/seller/products/" + productId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(productId));
        then(productService).should().updateProductInfo(eq(productId), any(), eq(request.toServiceRequest()));
    }
}
