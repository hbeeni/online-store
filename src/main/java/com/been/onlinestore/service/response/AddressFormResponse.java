package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.Address;

public record AddressFormResponse(
	Long id,
	String detail,
	String zipcode,
	boolean defaultAddress
) {

	public static AddressFormResponse of(Long id, String detail, String zipcode, boolean defaultAddress) {
		return new AddressFormResponse(id, detail, zipcode, defaultAddress);
	}

	public static AddressFormResponse from(Address entity) {
		return AddressFormResponse.of(
			entity.getId(),
			entity.getDetail(),
			entity.getZipcode(),
			entity.getDefaultAddress()
		);
	}
}
