package com.been.onlinestore.service.request;

import com.been.onlinestore.domain.Address;
import com.been.onlinestore.domain.User;

public record AddressServiceRequest(
		String detail,
		String zipcode,
		Boolean defaultAddress
) {

	public static AddressServiceRequest of(String detail, String zipcode, Boolean defaultAddress) {
		return new AddressServiceRequest(detail, zipcode, defaultAddress);
	}

	public Address toEntity(User user, boolean setDefaultAddress) {
		return Address.of(
				user,
				detail,
				zipcode,
				setDefaultAddress
		);
	}
}
