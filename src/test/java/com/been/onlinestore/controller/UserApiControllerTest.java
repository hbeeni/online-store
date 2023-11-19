package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.service.UserService;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 회원 (공통)")
@Import(TestSecurityConfig.class)
@WebMvcTest(UserApiController.class)
class UserApiControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean UserService userService;

    @WithUserDetails
    @DisplayName("[API][PUT] 회원 정보 변경 - 닉네임과 휴대폰 번호 변경 가능")
    @Test
    void test_updateUserInfo() throws Exception {
        //Given
        Long id = TestSecurityConfig.USER_ID;
        UserRequest.Update request = UserRequest.Update.builder()
                .nickname("nick")
                .phone("01011112222")
                .build();
        given(userService.updateInfo(id, request.nickname(), request.phone())).willReturn(id);

        //When & Then
        mvc.perform(
                    put("/api/users/info")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(id));
        then(userService).should().updateInfo(id, request.nickname(), request.phone());
    }
}
