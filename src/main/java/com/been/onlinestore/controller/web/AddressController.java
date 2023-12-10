package com.been.onlinestore.controller.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.been.onlinestore.controller.dto.AddressRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.AddressService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/addresses")
@Controller
public class AddressController {

	private final AddressService addressService;

	@GetMapping
	public String getAddresses(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
		model.addAttribute("addresses", addressService.findAddresses(principalDetails.id()));
		return "user/address/addresses";
	}

	@GetMapping("/new")
	public String addAddressForm(@ModelAttribute("address") AddressRequest request) {
		return "user/address/addForm";
	}

	@PostMapping("/new")
	public String addAddress(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@ModelAttribute("address") @Validated AddressRequest request,
		BindingResult bindingResult
	) {
		if (bindingResult.hasFieldErrors()) {
			return "user/address/addForm";
		}
		addressService.addAddress(principalDetails.id(), request.toServiceRequest());
		return "redirect:/addresses";
	}

	@GetMapping("/{addressId}/edit")
	public String updateAddressForm(
		@PathVariable Long addressId,
		@AuthenticationPrincipal PrincipalDetails principalDetails, Model model
	) {
		model.addAttribute("address", addressService.findAddressForm(addressId, principalDetails.id()));
		return "user/address/editForm";
	}

	@PostMapping("/{addressId}/edit")
	public String updateAddress(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable Long addressId,
		@ModelAttribute("address") @Validated AddressRequest request,
		BindingResult bindingResult
	) {
		if (bindingResult.hasFieldErrors()) {
			return "user/address/editForm";
		}
		addressService.updateAddress(addressId, principalDetails.id(), request.toServiceRequest());
		return "redirect:/addresses";
	}

	@PostMapping("/{addressId}/delete")
	public String deleteAddress(
		@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long addressId) {
		addressService.deleteAddress(addressId, principalDetails.id());
		return "redirect:/addresses";
	}
}
