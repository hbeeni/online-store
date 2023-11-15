package com.been.onlinestore.controller.user;

import com.been.onlinestore.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 주소 (일반)")
@Import(SecurityConfig.class)
@WebMvcTest(AddressApiController.class)
class AddressApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private static final long ID = 1L;
    private static final String CITY = "test city";
    private static final Boolean DEFAULT_ADDRESS = true;

    @DisplayName("[API][GET] 주소 리스트 조회")
    @Test
    void test_getAddressList() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(ID))
                .andExpect(jsonPath("$.data[0].city").value(CITY))
                .andExpect(jsonPath("$.data[0].defaultAddress").value(DEFAULT_ADDRESS));
    }

    @DisplayName("[API][GET] 주소 상세 조회")
    @Test
    void test_getAddress() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/api/addresses/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.city").value(CITY))
                .andExpect(jsonPath("$.data.defaultAddress").value(DEFAULT_ADDRESS));
    }

    @DisplayName("[API][POST] 주소 등록")
    @Test
    void test_addAddress() throws Exception {
        //Given

        //When & Then
        mvc.perform(post("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
    }

    @DisplayName("[API][PUT] 주소 수정")
    @Test
    void test_updateAddressInfo() throws Exception {
        //Given

        //When & Then
        mvc.perform(put("/api/addresses/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
    }

    @DisplayName("[API][DELETE] 주소 삭제")
    @Test
    void test_deleteAddress() throws Exception {
        //Given

        //When & Then
        mvc.perform(delete("/api/addresses/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
    }
}
