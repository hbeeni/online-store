package com.been.onlinestore.controller.web;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class AuthController {

	private final UserService userService;
	private final PasswordEncoder encoder;

	@GetMapping("/sign-up")
	public String signUpForm(@ModelAttribute("user") UserRequest.SignUp request) {
		return "user/sign-up";
	}

	@PostMapping("/sign-up")
	public String signUp(@ModelAttribute("user") @Validated UserRequest.SignUp request, BindingResult bindingResult) {
		if (bindingResult.hasFieldErrors()) {
			return "user/sign-up";
		}

		userService.signUp(request.toServiceRequest(), encoder.encode(request.password()));
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginForm(@ModelAttribute("user") UserRequest.Login request) {
		return "user/login";
	}
}
