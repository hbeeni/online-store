package com.been.onlinestore.controller.admin;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled("구현 전")
@DisplayName("API 컨트롤러 - 회원 (관리자)")
@Import(SecurityConfig.class)
@WebMvcTest(AdminUserApiController.class)
class AdminUserApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    private static final long ID = 1L;
    private static final String UID = "testId";
    private static final String NAME = "test name";

    @DisplayName("[API][GET] 회원 리스트 조회")
    @Test
    void test_getUserList() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/admin-api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(ID))
                .andExpect(jsonPath("$.data[0].uid").value(UID))
                .andExpect(jsonPath("$.data[0].name").value(NAME));
    }

    @DisplayName("[API][GET] 회원 리스트 조회 + 페이징")
    @Test
    void test_getUserList_withPagination() throws Exception {
        //Given
        String sortName = "name";
        String direction = "asc";
        int pageNumber = 0;
        int pageSize = 2;

        //When & Then
        mvc.perform(
                        get("/admin-api/users")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(ID))
                .andExpect(jsonPath("$.data[0].uid").value(UID))
                .andExpect(jsonPath("$.data[0].name").value(NAME))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @DisplayName("[API][GET] 회원 상세 조회")
    @Test
    void test_getUserInfo() throws Exception {
        //Given

        //When & Then
        mvc.perform(get("/admin-api/users/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID))
                .andExpect(jsonPath("$.data.uid").value(UID))
                .andExpect(jsonPath("$.data.name").value(NAME));
    }

    @DisplayName("[API][DELETE] 회원 삭제(강제 탈퇴)")
    @Test
    void test_deleteUser() throws Exception {
        //Given

        //When & Then
        mvc.perform(delete("/admin-api/users/" + ID))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(ID));
    }
}
