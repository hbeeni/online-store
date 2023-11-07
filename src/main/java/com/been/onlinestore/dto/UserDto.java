package com.been.onlinestore.dto;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String uid,
        String password,
        String name,
        String email,
        String nickname,
        String phone,
        RoleType roleType,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static UserDto of(String uid, String password, String name, String email, String nickname, String phone, RoleType roleType) {
        return UserDto.of(null, uid, password, name, email, nickname, phone, roleType, null, null);
    }

    public static UserDto of(Long id, String uid, String password, String name, String email, String nickname, String phone, RoleType roleType, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new UserDto(id, uid, password, name, email, nickname, phone, roleType, createdAt, modifiedAt);
    }

    public static UserDto from(User user) {
        return UserDto.of(
                user.getId(),
                user.getUid(),
                user.getPassword(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone(),
                user.getRoleType(),
                user.getCreatedAt(),
                user.getModifiedAt());
    }
}
