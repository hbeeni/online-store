package com.been.onlinestore.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.admin.AdminUserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

	private final AdminUserService adminUserService;

	@Override
	public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
		return adminUserService.searchUser(uid)
			.map(PrincipalDetails::from)
			.orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.NOT_FOUND_USER.getMessage()));
	}
}
