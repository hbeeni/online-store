package com.been.onlinestore.controller.dto.request;

import com.been.onlinestore.domain.constant.RoleType;

public record SignUpRequest(
        String uid,
        String password,
        String name,
        String email,
        String nickname,
        String phone,
        RoleType roleType
) {

    public static SignUpRequest of(String uid, String password, String name, String email, String nickname, String phone, RoleType roleType) {
        return new SignUpRequest(uid, password, name, email, nickname, phone, roleType);
    }
}
