package com.been.onlinestore.controller.dto.security;

import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.service.response.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public record PrincipalDetails(
        Long id,
        String uid,
        String password,
        RoleType roleType,
        String name,
        String email,
        String nickname,
        String phone
) implements UserDetails {

    public static PrincipalDetails of(Long id, String uid, String password, RoleType roleType, String name, String email, String nickname, String phone) {
        return new PrincipalDetails(
                id,
                uid,
                password,
                roleType,
                name,
                email,
                nickname,
                phone);
    }

    public static PrincipalDetails from(UserResponse userResponse) {
        return PrincipalDetails.of(
                userResponse.id(),
                userResponse.uid(),
                userResponse.password(),
                userResponse.roleType(),
                userResponse.name(),
                userResponse.email(),
                userResponse.nickname(),
                userResponse.phone()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(roleType.getRoleName()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return uid;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
