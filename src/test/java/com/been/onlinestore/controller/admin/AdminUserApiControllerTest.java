package com.been.onlinestore.controller.admin;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.service.UserService;
import com.been.onlinestore.service.response.UserResponse;
import org.junit.jupiter.api.Disabled;
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

import static com.been.onlinestore.util.UserTestDataUtil.createUserResponse;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 회원 (관리자)")
@Import(TestSecurityConfig.class)
@WebMvcTest(AdminUserApiController.class)
class AdminUserApiControllerTest {

    @Autowired private MockMvc mvc;

    @MockBean UserService userService;

    @DisplayName("[API][GET] 회원 리스트 조회 + 페이징")
    @Test
    void test_getUsers_withPagination() throws Exception {
        //Given
        long id = 1L;
        String uid = "testId";

        String sortName = "name";
        String direction = "asc";
        int pageNumber = 0;
        int pageSize = 10;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(sortName)));
        UserResponse response = createUserResponse(id, uid);
        Page<UserResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        given(userService.findUsers(pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                        get("/api/admin/users")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(response.id()))
                .andExpect(jsonPath("$.data[0].uid").value(response.uid()))
                .andExpect(jsonPath("$.data[0].name").value(response.name()))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
        then(userService).should().findUsers(pageable);
    }

    @DisplayName("[API][GET] 회원 상세 조회")
    @Test
    void test_getUser() throws Exception {
        //Given
        long id = 1L;
        String uid = "testId";
        UserResponse response = createUserResponse(id, uid);

        given(userService.findUser(id)).willReturn(response);

        //When & Then
        mvc.perform(get("/api/admin/users/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(response.id()))
                .andExpect(jsonPath("$.data.uid").value(response.uid()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
        then(userService).should().findUser(id);
    }

    @Disabled("마지막에 구현")
    @DisplayName("[API][DELETE] 회원 삭제(강제 탈퇴)")
    @Test
    void test_deleteUser() throws Exception {
        //Given
        long id = 1L;
        String uid = "testId";
        String name = "test name";

        //When & Then
        mvc.perform(delete("/api/admin/users/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(id));
    }
}
