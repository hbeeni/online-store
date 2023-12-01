package com.been.onlinestore.service.response;

import com.been.onlinestore.domain.Address;

public record AddressResponse(
		Long id,
		String detail,
		String zipcode,
		String defaultAddress
) {

	public static AddressResponse of(Long id, String detail, String zipcode, String defaultAddress) {
		return new AddressResponse(id, detail, zipcode, defaultAddress);
	}

	public static AddressResponse from(Address entity) {
		return AddressResponse.of(
				entity.getId(),
				entity.getDetail(),
				entity.getZipcode(),
				entity.getDefaultAddress() ? "Y" : "N"
		);
	}
}
