package com.been.onlinestore.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.controller.dto.AddressRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.response.ApiResponse;
import com.been.onlinestore.service.AddressService;
import com.been.onlinestore.service.dto.response.AddressResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/addresses")
@RestController
public class AddressApiController {

	private final AddressService addressService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		return ResponseEntity.ok(ApiResponse.success(addressService.findAddresses(principalDetails.id())));
	}

	@GetMapping("/{addressId}")
	public ResponseEntity<ApiResponse<AddressResponse>> getAddress(
		@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long addressId
	) {
		return ResponseEntity.ok(ApiResponse.success(addressService.findAddress(addressId, principalDetails.id())));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Map<String, Long>>> addAddress(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@RequestBody @Validated AddressRequest request
	) {
		return ResponseEntity.ok(
			ApiResponse.successId(addressService.addAddress(principalDetails.id(), request.toServiceRequest())));
	}

	@PutMapping("/{addressId}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> updateAddress(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable Long addressId,
		@RequestBody @Validated AddressRequest request
	) {
		return ResponseEntity.ok(ApiResponse.successId(
			addressService.updateAddress(addressId, principalDetails.id(), request.toServiceRequest())));
	}

	@DeleteMapping("/{addressId}")
	public ResponseEntity<ApiResponse<Map<String, Long>>> deleteAddress(
		@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long addressId
	) {
		return ResponseEntity.ok(ApiResponse.successId(addressService.deleteAddress(addressId, principalDetails.id())));
	}
}
