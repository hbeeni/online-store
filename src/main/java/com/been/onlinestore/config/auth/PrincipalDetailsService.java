package com.been.onlinestore.config.auth;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
		return userService.searchUser(uid)
				.map(PrincipalDetails::from)
				.orElseThrow(() -> new UsernameNotFoundException(ErrorMessages.NOT_FOUND_USER.getMessage()));
	}
}
