package com.been.onlinestore.controller.dto;

import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.service.request.UserServiceRequest;
import lombok.Builder;

public record UserRequest() {

    @Builder
    public record SignUp(
            String uid,
            String password,
            String name,
            String email,
            String nickname,
            String phone,
            RoleType roleType
    ) {

        public static SignUp of(String uid, String password, String name, String email, String nickname, String phone, RoleType roleType) {
            return new SignUp(uid, password, name, email, nickname, phone, roleType);
        }

        public UserServiceRequest.SignUp toServiceRequest() {
            return UserServiceRequest.SignUp.of(
                    uid,
                    password,
                    name,
                    email,
                    nickname,
                    phone,
                    roleType
            );
        }
    }

    @Builder
    public record Login(
            String uid,
            String password
    ) {}
}
