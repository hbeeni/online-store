package com.been.onlinestore.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.been.onlinestore.util.ApiResponseUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
		throws IOException, ServletException {
		if (authentication != null) {
			log.info("로그아웃 성공. uid = {}", ((UserDetails)authentication.getPrincipal()).getUsername());
		}
		ApiResponseUtils.setSuccessResponse(response, HttpStatus.OK);
	}
}
