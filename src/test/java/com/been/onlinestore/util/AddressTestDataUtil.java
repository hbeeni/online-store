package com.been.onlinestore.util;

import static com.been.onlinestore.util.UserTestDataUtil.*;

import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.domain.Address;
import com.been.onlinestore.service.dto.request.AddressServiceRequest;

public class AddressTestDataUtil {

	public static Address createAddress() {
		return createAddress(1L);
	}

	public static Address createAddress(Long id) {
		return createAddress(id, false);
	}

	public static Address createAddress(Long id, boolean defaultAddress) {
		Address address = Address.of(
			createUser("user"),
			"detail",
			"zipcode",
			defaultAddress
		);
		ReflectionTestUtils.setField(address, "id", id);
		return address;
	}

	public static AddressServiceRequest createAddressServiceRequest(boolean defaultAddress) {
		return createAddressServiceRequest("update detail", "update zipcode", defaultAddress);
	}

	public static AddressServiceRequest createAddressServiceRequest(
		String detail, String zipcode, boolean defaultAddress
	) {
		return AddressServiceRequest.of(detail, zipcode, defaultAddress);
	}
}
