package com.been.onlinestore.controller.seller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.been.onlinestore.util.ProductTestDataUtil.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static java.time.LocalDateTime.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.ProductRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.ProductService;

@DisplayName("API 컨트롤러 - 상품 (판매자)")
@Import(TestSecurityConfig.class)
@WebMvcTest(SellerProductApiController.class)
class SellerProductApiControllerTest extends RestDocsSupport {

	@MockBean
	private ProductService productService;
	@MockBean
	private ImageStore imageStore;

	@Value("${image.path}")
	private String imagePath;

	@WithUserDetails
	@DisplayName("[API][GET] 상품 리스트 조회 + 페이징")
	@Test
	void test_getProductList_withPagination() throws Exception {
		//Given
		long productId = 1L;
		String name = "test product";

		String sortName = "id";
		String direction = "asc";
		int pageNumber = 0;
		int pageSize = 10;

		ProductSearchCondition cond = ProductSearchCondition.of(null, null, null);
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(sortName)));
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
		String searchStatus = "SALE";
		String sortName = "id";
		String direction = "asc";
		int pageNumber = 0;
		int pageSize = 10;

		ProductSearchCondition cond = ProductSearchCondition.of(null, null, SaleStatus.SALE);
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(sortName)));
		AdminProductResponse response1 = AdminProductResponse.of(
				2L,
				"패션의류",
				AdminProductResponse.Seller.of(1L, "sellerA"),
				"꽃무늬 바지",
				15000,
				"이쁜 꽃무늬 바지입니다.",
				930,
				1002,
				SaleStatus.SALE,
				2500,
				imagePath + UUID.randomUUID() + ".png",
				now().minusMonths(6),
				"sellerA",
				now().minusMonths(1),
				"sellerA"
		);
		AdminProductResponse response2 = AdminProductResponse.of(
				5L,
				"식품",
				AdminProductResponse.Seller.of(1L, "sellerA"),
				"요거트",
				3500,
				"맛있는 그릭 요거트",
				56,
				22,
				SaleStatus.SALE,
				3000,
				imagePath + UUID.randomUUID() + ".png",
				now().minusMonths(2),
				"sellerA",
				now().minusMonths(1),
				"sellerA"
		);
		List<AdminProductResponse> content = List.of(response1, response2);
		Page<AdminProductResponse> page = new PageImpl<>(content, pageable, content.size());

		given(productService.findProductsForSeller(any(), eq(cond), eq(pageable))).willReturn(page);

		//When & Then
		mvc.perform(
						get("/api/seller/products")
								.queryParam("saleStatus", searchStatus)
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
						"seller/product/getProducts-searching",
						sellerApiDescription(TagDescription.PRODUCT, "상품 페이징 조회 + 검색"),
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
								fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
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
		then(productService).should().findProductsForSeller(any(), eq(cond), eq(pageable));
	}

	@WithUserDetails
	@DisplayName("[API][GET] 상품 상세 조회")
	@Test
	void test_getProduct() throws Exception {
		//Given
		long productId = 1L;
		AdminProductResponse response = AdminProductResponse.of(
				2L,
				"패션의류",
				AdminProductResponse.Seller.of(1L, "sellerA"),
				"꽃무늬 바지",
				15000,
				"이쁜 꽃무늬 바지입니다.",
				930,
				1002,
				SaleStatus.SALE,
				2500,
				imagePath + UUID.randomUUID() + ".png",
				now().minusMonths(6),
				"sellerA",
				now().minusMonths(1),
				"sellerA"
		);

		given(productService.findProductForSeller(eq(productId), any())).willReturn(response);

		//When & Then
		mvc.perform(get("/api/seller/products/{productId}", productId))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.data.id").value(response.id()))
				.andExpect(jsonPath("$.data.name").value(response.name()))
				.andExpect(jsonPath("$.data.price").value(response.price()))
				.andDo(document(
						"seller/product/getProduct",
						sellerApiDescription(TagDescription.PRODUCT, "상품 상세 조회"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
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
								fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
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
		then(productService).should().findProductForSeller(eq(productId), any());
	}

	@WithUserDetails
	@DisplayName("[API][POST] 상품 등록")
	@Test
	void test_addProduct() throws Exception {
		//Given
		long productId = 1L;
		ProductRequest.Create data = ProductRequest.Create.builder()
				.categoryId(1L)
				.name("name")
				.price(10000)
				.stockQuantity(1000)
				.saleStatus(SaleStatus.SALE)
				.deliveryFee(2500)
				.build();
		String imageName = UUID.randomUUID() + ".png";
		MockMultipartFile image = new MockMultipartFile("image", imageName, MediaType.IMAGE_PNG_VALUE,
				"image".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile request = new MockMultipartFile("request", "request", MediaType.APPLICATION_JSON_VALUE,
				mapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8));

		given(imageStore.saveImage(image)).willReturn(imageName);
		given(productService.addProduct(any(), eq(data.toServiceRequest()), eq(imageName))).willReturn(productId);

		//When & Then
		mvc.perform(
						multipart("/api/seller/products")
								.file(request)
								.file(image)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.data.id").value(productId))
				.andDo(document(
						"seller/product/addProduct",
						sellerApiDescription(TagDescription.PRODUCT, "상품 등록"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParts(
								partWithName("request").description("상품 데이터"),
								partWithName("image").description(PRODUCT_IMAGE.getDescription())
						),
						requestPartFields(
								"request",
								fieldWithPath("categoryId").type(JsonFieldType.NUMBER)
										.description(CATEGORY_ID.getDescription()),
								fieldWithPath("name").type(JsonFieldType.STRING)
										.description(PRODUCT_NAME.getDescription()),
								fieldWithPath("price").type(JsonFieldType.NUMBER)
										.description(PRODUCT_PRICE.getDescription()),
								fieldWithPath("description").type(JsonFieldType.VARIES)
										.description(PRODUCT_DESCRIPTION.getDescription())
										.optional(),
								fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER)
										.description(PRODUCT_STOCK_QUANTITY.getDescription()),
								fieldWithPath("saleStatus").type(JsonFieldType.VARIES)
										.description(PRODUCT_SALE_STATUS.getDescription())
										.optional(),
								fieldWithPath("deliveryFee").type(JsonFieldType.NUMBER)
										.description(PRODUCT_DELIVERY_FEE.getDescription())
						),
						responseFields(
								RestDocsUtils.STATUS,
								fieldWithPath("data.id").type(JsonFieldType.NUMBER)
										.description(ADD.getDescription() + PRODUCT_ID.getDescription())
						)
				));
		then(productService).should().addProduct(any(), eq(data.toServiceRequest()), eq(imageName));
	}

	@WithUserDetails
	@DisplayName("[API][PUT] 상품 정보 수정")
	@Test
	void test_updateProductInfo() throws Exception {
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

		given(productService.updateProductInfo(eq(productId), anyLong(), eq(request.toServiceRequest()))).willReturn(
				productId);

		//When & Then
		mvc.perform(
						put("/api/seller/products/{productId}", productId)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.characterEncoding("UTF-8")
								.content(mapper.writeValueAsString(request))
				)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.data.id").value(productId))
				.andDo(document(
						"seller/product/updateProductInfo",
						sellerApiDescription(TagDescription.PRODUCT, "상품 정보 수정"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("categoryId").type(JsonFieldType.NUMBER)
										.description(CATEGORY_ID.getDescription()),
								fieldWithPath("name").type(JsonFieldType.STRING)
										.description(PRODUCT_NAME.getDescription()),
								fieldWithPath("price").type(JsonFieldType.NUMBER)
										.description(PRODUCT_PRICE.getDescription()),
								fieldWithPath("description").type(JsonFieldType.VARIES)
										.description(PRODUCT_DESCRIPTION.getDescription())
										.optional(),
								fieldWithPath("stockQuantity").type(JsonFieldType.NUMBER)
										.description(PRODUCT_STOCK_QUANTITY.getDescription()),
								fieldWithPath("saleStatus").type(JsonFieldType.STRING)
										.description(PRODUCT_SALE_STATUS.getDescription()),
								fieldWithPath("deliveryFee").type(JsonFieldType.NUMBER)
										.description(PRODUCT_DELIVERY_FEE.getDescription()),
								fieldWithPath("imageUrl").type(JsonFieldType.STRING)
										.description(PRODUCT_IMAGE_URL.getDescription())
										.optional()
						),
						responseFields(
								RestDocsUtils.STATUS,
								fieldWithPath("data.id").type(JsonFieldType.NUMBER)
										.description("수정한 " + PRODUCT_ID.getDescription())
						)
				));
		then(productService).should().updateProductInfo(eq(productId), anyLong(), eq(request.toServiceRequest()));
	}

	@WithUserDetails
	@DisplayName("[API][POST] 상품 이미지 수정")
	@Test
	void test_updateProductImage() throws Exception {
		//Given
		long productId = 1L;
		String imageName = UUID.randomUUID() + ".png";
		MockMultipartFile image = new MockMultipartFile("image", imageName, MediaType.IMAGE_PNG_VALUE,
				"image".getBytes(StandardCharsets.UTF_8));

		MockMultipartHttpServletRequestBuilder builder = RestDocumentationRequestBuilders.multipart(
				"/api/seller/products/{productId}/img", productId);
		builder.with(request -> {
			request.setMethod("PUT");
			return request;
		});

		given(imageStore.saveImage(image)).willReturn(imageName);
		given(productService.updateProductImage(eq(productId), anyLong(), eq(imageName))).willReturn(productId);

		//When & Then
		mvc.perform(builder.file(image))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.data.id").value(productId))
				.andDo(document(
						"seller/product/updateProductInfo",
						sellerApiDescription(TagDescription.PRODUCT, "상품 이미지 수정"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestParts(
								partWithName("image").description(PRODUCT_IMAGE.getDescription())
						),
						responseFields(
								RestDocsUtils.STATUS,
								fieldWithPath("data.id").type(JsonFieldType.NUMBER)
										.description(ADD.getDescription() + PRODUCT_ID.getDescription())
						)
				));
		then(productService).should().updateProductImage(eq(productId), anyLong(), eq(imageName));
	}
}
