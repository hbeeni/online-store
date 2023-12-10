package com.been.onlinestore.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UserController {

	private final UserService userService;

	@GetMapping("/info")
	public String getUserInfoForm(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
		model.addAttribute("user", userService.findUser(principalDetails.id()));
		return "user/userInfoForm";
	}

	@PostMapping("/info")
	public String updateUserInfo(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@ModelAttribute("user") @Validated UserRequest.UpdateForm request,
		BindingResult bindingResult,
		RedirectAttributes redirectAttributes
	) {
		if (bindingResult.hasFieldErrors()) {
			return "user/userInfoForm";
		}

		userService.updateInfo(principalDetails.id(), request.nickname(), request.phone());
		redirectAttributes.addFlashAttribute("status", true);
		return "redirect:/users/info";
	}
}
