package com.been.onlinestore.controller.admin;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.been.onlinestore.util.ProductTestDataUtil.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static java.time.LocalDateTime.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.ProductService;

@DisplayName("API 컨트롤러 - 상품 (관리자)")
@Import(TestSecurityConfig.class)
@WebMvcTest(AdminProductApiController.class)
class AdminProductApiControllerTest extends RestDocsSupport {

	@MockBean
	private ProductService productService;

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
		String searchName = "꽃무늬";
		String sortName = "id";
		String direction = "asc";
		int pageNumber = 0;
		int pageSize = 10;

		ProductSearchCondition cond = ProductSearchCondition.of(null, searchName, null);
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(sortName)));
		AdminProductResponse response1 = AdminProductResponse.of(
				1L,
				"상의",
				AdminProductResponse.Seller.of(1L, "sellerA"),
				"꽃무늬 셔츠",
				12000,
				"이쁜 꽃무늬 셔츠입니다.",
				1200,
				142343,
				SaleStatus.CLOSE,
				3000,
				null,
				now().minusYears(1),
				"sellerA",
				now().minusMonths(2),
				"sellerA"
		);
		AdminProductResponse response2 = AdminProductResponse.of(
				2L,
				"하의",
				AdminProductResponse.Seller.of(2L, "sellerB"),
				"꽃무늬 바지",
				15000,
				"이쁜 꽃무늬 바지입니다.",
				930,
				1002,
				SaleStatus.SALE,
				2500,
				null,
				now().minusMonths(6),
				"sellerB",
				now().minusMonths(1),
				"sellerB"
		);
		List<AdminProductResponse> content = List.of(response1, response2);
		Page<AdminProductResponse> page = new PageImpl<>(content, pageable, content.size());

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
				.andExpect(jsonPath("$.data[0].id").value(response1.id()))
				.andExpect(jsonPath("$.data[0].name").value(response1.name()))
				.andExpect(jsonPath("$.data[0].price").value(response1.price()))
				.andExpect(jsonPath("$.data[0].seller").exists())
				.andExpect(jsonPath("$.data[0].createdAt").exists())
				.andExpect(jsonPath("$.page.number").value(page.getNumber()))
				.andExpect(jsonPath("$.page.size").value(page.getSize()))
				.andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
				.andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()))
				.andDo(document(
						"admin/product/getProducts-searching",
						adminApiDescription(TagDescription.PRODUCT, "상품 페이징 조회 + 검색"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParameters(PAGE_REQUEST_PARAM)
								.and(
										parameterWithName("categoryId").description(
												"검색할 상품의 " + CATEGORY_ID.getDescription()).optional(),
										parameterWithName("name").description("검색할 " + PRODUCT_NAME.getDescription())
												.optional(),
										parameterWithName("saleStatus").description(
												"검색할 " + PRODUCT_SALE_STATUS.getDescription()).optional()
								),
						responseFields(
								RestDocsUtils.STATUS,
								fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
										.description(PRODUCT_ID.getDescription()),
								fieldWithPath("data[].category").type(JsonFieldType.VARIES)
										.description(CATEGORY_NAME.getDescription()),
								fieldWithPath("data[].seller.id").type(JsonFieldType.NUMBER)
										.description(SELLER_ID.getDescription()),
								fieldWithPath("data[].seller.uid").type(JsonFieldType.STRING)
										.description(SELLER_UID.getDescription()),
								fieldWithPath("data[].name").type(JsonFieldType.STRING)
										.description(PRODUCT_NAME.getDescription()),
								fieldWithPath("data[].price").type(JsonFieldType.NUMBER)
										.description(PRODUCT_PRICE.getDescription()),
								fieldWithPath("data[].description").type(JsonFieldType.VARIES)
										.description(PRODUCT_DESCRIPTION.getDescription()),
								fieldWithPath("data[].stockQuantity").type(JsonFieldType.NUMBER)
										.description(PRODUCT_STOCK_QUANTITY.getDescription()),
								fieldWithPath("data[].salesVolume").type(JsonFieldType.NUMBER)
										.description(PRODUCT_SALES_VOLUME.getDescription()),
								fieldWithPath("data[].saleStatus").type(JsonFieldType.STRING)
										.description(PRODUCT_SALE_STATUS.getDescription()),
								fieldWithPath("data[].deliveryFee").type(JsonFieldType.NUMBER)
										.description(PRODUCT_DELIVERY_FEE.getDescription()),
								fieldWithPath("data[].imageUrl").type(JsonFieldType.VARIES)
										.description(PRODUCT_IMAGE_URL.getDescription()),
								fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
										.description(CREATED_AT.getDescription()),
								fieldWithPath("data[].createdBy").type(JsonFieldType.STRING)
										.description(CREATED_BY.getDescription()),
								fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING)
										.description(MODIFIED_AT.getDescription()),
								fieldWithPath("data[].modifiedBy").type(JsonFieldType.STRING)
										.description(MODIFIED_BY.getDescription())
						).and(PAGE_INFO)
				));
		then(productService).should().findProductsForAdmin(cond, pageable);
	}

	@DisplayName("[API][GET] 상품 상세 조회")
	@Test
	void test_getProduct() throws Exception {
		//Given
		AdminProductResponse response = AdminProductResponse.of(
				1L,
				"상의",
				AdminProductResponse.Seller.of(1L, "sellerA"),
				"꽃무늬 셔츠",
				12000,
				"이쁜 꽃무늬 셔츠입니다.",
				1200,
				142343,
				SaleStatus.CLOSE,
				3000,
				null,
				now().minusYears(1),
				"sellerA",
				now().minusMonths(2),
				"sellerA"
		);
		given(productService.findProductForAdmin(response.id())).willReturn(response);

		//When & Then
		mvc.perform(get("/api/admin/products/{productId}", response.id()))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.data.id").value(response.id()))
				.andExpect(jsonPath("$.data.name").value(response.name()))
				.andExpect(jsonPath("$.data.price").value(response.price()))
				.andDo(document(
						"admin/product/getProduct",
						adminApiDescription(TagDescription.PRODUCT, "상품 상세 조회"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("productId").description(PRODUCT_ID.getDescription())
						),
						responseFields(
								RestDocsUtils.STATUS,
								fieldWithPath("data.id").type(JsonFieldType.NUMBER)
										.description(PRODUCT_ID.getDescription()),
								fieldWithPath("data.category").type(JsonFieldType.VARIES)
										.description(CATEGORY_NAME.getDescription()),
								fieldWithPath("data.seller.id").type(JsonFieldType.NUMBER)
										.description(SELLER_ID.getDescription()),
								fieldWithPath("data.seller.uid").type(JsonFieldType.STRING)
										.description(SELLER_UID.getDescription()),
								fieldWithPath("data.name").type(JsonFieldType.STRING)
										.description(PRODUCT_NAME.getDescription()),
								fieldWithPath("data.price").type(JsonFieldType.NUMBER)
										.description(PRODUCT_PRICE.getDescription()),
								fieldWithPath("data.description").type(JsonFieldType.VARIES)
										.description(PRODUCT_DESCRIPTION.getDescription()),
								fieldWithPath("data.stockQuantity").type(JsonFieldType.NUMBER)
										.description(PRODUCT_STOCK_QUANTITY.getDescription()),
								fieldWithPath("data.salesVolume").type(JsonFieldType.NUMBER)
										.description(PRODUCT_SALES_VOLUME.getDescription()),
								fieldWithPath("data.saleStatus").type(JsonFieldType.STRING)
										.description(PRODUCT_SALE_STATUS.getDescription()),
								fieldWithPath("data.deliveryFee").type(JsonFieldType.NUMBER)
										.description(PRODUCT_DELIVERY_FEE.getDescription()),
								fieldWithPath("data.imageUrl").type(JsonFieldType.VARIES)
										.description(PRODUCT_IMAGE_URL.getDescription()),
								fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
										.description(CREATED_AT.getDescription()),
								fieldWithPath("data.createdBy").type(JsonFieldType.STRING)
										.description(CREATED_BY.getDescription()),
								fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING)
										.description(MODIFIED_AT.getDescription()),
								fieldWithPath("data.modifiedBy").type(JsonFieldType.STRING)
										.description(MODIFIED_BY.getDescription())
						)
				));
		then(productService).should().findProductForAdmin(response.id());
	}
}
