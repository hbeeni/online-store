package com.been.onlinestore.controller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.AddressRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.service.AddressService;
import com.been.onlinestore.service.dto.response.AddressResponse;

@DisplayName("API 컨트롤러 - 주소 (배송지)")
@Import(TestSecurityConfig.class)
@WebMvcTest(AddressApiController.class)
class AddressApiControllerTest extends RestDocsSupport {

	@MockBean
	AddressService addressService;

	@WithUserDetails
	@DisplayName("[API][GET] 배송지 리스트 조회")
	@Test
	void test_getAddresses() throws Exception {
		//Given
		long userId = TestSecurityConfig.USER_ID;

		AddressResponse response1 = AddressResponse.of(
			1L,
			"서울 종로구 청와대로 1",
			"03048",
			"Y"
		);
		AddressResponse response2 = AddressResponse.of(
			2L,
			"서울 중구 세종대로 110 서울특별시청",
			"04524",
			"N"
		);

		given(addressService.findAddresses(userId)).willReturn(List.of(response1, response2));

		//When & Then
		mvc.perform(get("/api/addresses"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].id").value(response1.id()))
			.andExpect(jsonPath("$.data[0].detail").value(response1.detail()))
			.andExpect(jsonPath("$.data[0].defaultAddress").value(response1.defaultAddress()))
			.andDo(document(
				"user/address/getAddresses",
				userApiDescription(TagDescription.ADDRESS, "배송지 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					STATUS,
					fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
						.description(ADDRESS_ID.getDescription()),
					fieldWithPath("data[].detail").type(JsonFieldType.STRING)
						.description(ADDRESS_DETAIL.getDescription()),
					fieldWithPath("data[].zipcode").type(JsonFieldType.STRING)
						.description(ADDRESS_ZIPCODE.getDescription()),
					fieldWithPath("data[].defaultAddress").type(JsonFieldType.STRING)
						.description(ADDRESS_DEFAULT_ADDRESS.getDescription())
				)
			));
		then(addressService).should().findAddresses(userId);
	}

	@WithUserDetails
	@DisplayName("[API][GET] 배송지 상세 조회")
	@Test
	void test_getAddress() throws Exception {
		//Given
		long userId = TestSecurityConfig.USER_ID;
		AddressResponse response = AddressResponse.of(
			1L,
			"서울 종로구 청와대로 1",
			"03048",
			"Y"
		);

		given(addressService.findAddress(response.id(), userId)).willReturn(response);

		//When & Then
		mvc.perform(get("/api/addresses/{addressId}", response.id()))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(response.id()))
			.andExpect(jsonPath("$.data.detail").value(response.detail()))
			.andExpect(jsonPath("$.data.defaultAddress").value(response.defaultAddress()))
			.andDo(document(
				"user/address/getAddress",
				userApiDescription(TagDescription.ADDRESS, "배송지 상세 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("addressId").description(ADDRESS_ID.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(ADDRESS_ID.getDescription()),
					fieldWithPath("data.detail").type(JsonFieldType.STRING)
						.description(ADDRESS_DETAIL.getDescription()),
					fieldWithPath("data.zipcode").type(JsonFieldType.STRING)
						.description(ADDRESS_ZIPCODE.getDescription()),
					fieldWithPath("data.defaultAddress").type(JsonFieldType.STRING)
						.description(ADDRESS_DEFAULT_ADDRESS.getDescription())
				)
			));
		then(addressService).should().findAddress(response.id(), userId);
	}

	@WithUserDetails
	@DisplayName("[API][POST] 배송지 등록")
	@Test
	void test_addAddress() throws Exception {
		//Given
		long addressId = 1L;
		long userId = TestSecurityConfig.USER_ID;

		AddressRequest request = new AddressRequest("서울 종로구 청와대로 1", "03048", true);
		given(addressService.addAddress(userId, request.toServiceRequest())).willReturn(addressId);

		//When & Then
		mvc.perform(
				post("/api/addresses")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(addressId))
			.andDo(document(
				"user/address/addAddress",
				userApiDescription(TagDescription.ADDRESS, "배송지 추가"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("detail").type(JsonFieldType.STRING)
						.description(ADDRESS_DETAIL.getDescription()),
					fieldWithPath("zipcode").type(JsonFieldType.STRING)
						.description(ADDRESS_ZIPCODE.getDescription()),
					fieldWithPath("defaultAddress").type(JsonFieldType.BOOLEAN)
						.description(ADDRESS_DEFAULT_ADDRESS.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(ADD.getDescription() + ADDRESS_ID.getDescription())
				)
			));
		then(addressService).should().addAddress(userId, request.toServiceRequest());
	}

	@WithUserDetails
	@DisplayName("[API][PUT] 배송지 수정")
	@Test
	void test_updateAddress() throws Exception {
		//Given
		long addressId = 1L;
		long userId = TestSecurityConfig.USER_ID;

		AddressRequest request = new AddressRequest("서울 종로구 청와대로 1", "03048", true);
		given(addressService.updateAddress(addressId, userId, request.toServiceRequest())).willReturn(addressId);

		//When & Then
		mvc.perform(
				put("/api/addresses/{addressId}", addressId)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(addressId))
			.andDo(document(
				"user/address/updateAddress",
				userApiDescription(TagDescription.ADDRESS, "배송지 정보 수정"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("addressId").description(ADDRESS_ID.getDescription())
				),
				requestFields(
					fieldWithPath("detail").type(JsonFieldType.STRING)
						.description(ADDRESS_DETAIL.getDescription()),
					fieldWithPath("zipcode").type(JsonFieldType.STRING)
						.description(ADDRESS_ZIPCODE.getDescription()),
					fieldWithPath("defaultAddress").type(JsonFieldType.BOOLEAN)
						.description(ADDRESS_DEFAULT_ADDRESS.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(UPDATE.getDescription() + ADDRESS_ID.getDescription())
				)
			));
		then(addressService).should().updateAddress(addressId, userId, request.toServiceRequest());
	}

	@WithUserDetails
	@DisplayName("[API][DELETE] 배송지 삭제")
	@Test
	void test_deleteAddress() throws Exception {
		//Given
		long addressId = 1L;
		long userId = TestSecurityConfig.USER_ID;

		given(addressService.deleteAddress(addressId, userId)).willReturn(addressId);

		//When & Then
		mvc.perform(delete("/api/addresses/{addressId}", addressId))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(addressId))
			.andDo(document(
				"user/address/deleteAddress",
				userApiDescription(TagDescription.ADDRESS, "배송지 삭제"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("addressId").description(ADDRESS_ID.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(DELETE.getDescription() + ADDRESS_ID.getDescription())
				)
			));
		then(addressService).should().deleteAddress(addressId, userId);
	}
}
