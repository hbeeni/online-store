package com.been.onlinestore.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.been.onlinestore.service.request.AddressServiceRequest;

import lombok.Builder;

@Builder
public record AddressRequest(
		@NotBlank @Size(max = 50)
		String detail,

		@NotBlank @Size(max = 20)
		String zipcode,

		@NotNull
		Boolean defaultAddress
) {

	public AddressServiceRequest toServiceRequest() {
		return AddressServiceRequest.of(
				detail,
				zipcode,
				defaultAddress
		);
	}
}
