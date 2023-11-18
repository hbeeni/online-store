package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String uid,
        @JsonIgnore String password,
        String name,
        String email,
        String nickname,
        String phone,
        RoleType roleType,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static UserResponse of(Long id, String uid, String password, String name, String email, String nickname, String phone, RoleType roleType, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new UserResponse(id, uid, password, name, email, nickname, phone, roleType, createdAt, modifiedAt);
    }

    public static UserResponse from(User entity) {
        return UserResponse.of(
                entity.getId(),
                entity.getUid(),
                entity.getPassword(),
                entity.getName(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getPhone(),
                entity.getRoleType(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
