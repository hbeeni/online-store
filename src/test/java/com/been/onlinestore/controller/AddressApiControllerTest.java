package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.AddressRequest;
import com.been.onlinestore.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.been.onlinestore.util.AddressTestDataUtil.createAddressResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 주소 (배송지)")
@Import(TestSecurityConfig.class)
@WebMvcTest(AddressApiController.class)
class AddressApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    
    @MockBean AddressService addressService;

    @WithUserDetails
    @DisplayName("[API][GET] 배송지 리스트 조회")
    @Test
    void test_getAddresses() throws Exception {
        //Given
        long addressId = 1L;
        long userId = TestSecurityConfig.USER_ID;
        String detail = "test detail";
        String defaultAddress = "Y";

        given(addressService.findAddresses(userId)).willReturn(List.of(createAddressResponse(addressId, detail, defaultAddress)));

        //When & Then
        mvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(addressId))
                .andExpect(jsonPath("$.data[0].detail").value(detail))
                .andExpect(jsonPath("$.data[0].defaultAddress").value(defaultAddress));
        then(addressService).should().findAddresses(userId);
    }

    @WithUserDetails
    @DisplayName("[API][GET] 배송지 상세 조회")
    @Test
    void test_getAddress() throws Exception {
        //Given
        long addressId = 1L;
        long userId = TestSecurityConfig.USER_ID;
        String detail = "test detail";
        String defaultAddress = "Y";

        given(addressService.findAddress(addressId, userId)).willReturn(createAddressResponse(addressId, detail, defaultAddress));

        //When & Then
        mvc.perform(get("/api/addresses/" + addressId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(addressId))
                .andExpect(jsonPath("$.data.detail").value(detail))
                .andExpect(jsonPath("$.data.defaultAddress").value(defaultAddress));
        then(addressService).should().findAddress(addressId, userId);
    }

    @WithUserDetails
    @DisplayName("[API][POST] 배송지 등록")
    @Test
    void test_addAddress() throws Exception {
        //Given
        long addressId = 1L;
        long userId = TestSecurityConfig.USER_ID;
        String detail = "detail";
        String zipcode = "12345";
        boolean defaultAddress = true;

        AddressRequest request = AddressRequest.builder()
                .detail(detail)
                .zipcode(zipcode)
                .defaultAddress(defaultAddress)
                .build();
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
                .andExpect(jsonPath("$.data.id").value(addressId));
        then(addressService).should().addAddress(userId, request.toServiceRequest());
    }

    @WithUserDetails
    @DisplayName("[API][PUT] 배송지 수정")
    @Test
    void test_updateAddress() throws Exception {
        //Given
        long addressId = 1L;
        long userId = TestSecurityConfig.USER_ID;
        String detail = "update detail";
        String zipcode = "11111";
        boolean defaultAddress = true;

        AddressRequest request = AddressRequest.builder()
                .detail(detail)
                .zipcode(zipcode)
                .defaultAddress(defaultAddress)
                .build();
        given(addressService.updateAddress(addressId, userId, request.toServiceRequest())).willReturn(addressId);

        //When & Then
        mvc.perform(
                        put("/api/addresses/" + addressId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(addressId));
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
        mvc.perform(delete("/api/addresses/" + addressId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(addressId));
        then(addressService).should().deleteAddress(addressId, userId);
    }
}
