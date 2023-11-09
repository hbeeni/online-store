package com.been.onlinestore.util;

import com.been.onlinestore.domain.Address;
import com.been.onlinestore.dto.AddressDto;
import com.been.onlinestore.dto.UserDto;
import org.springframework.test.util.ReflectionTestUtils;

import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static java.time.LocalDateTime.now;

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

    public static Address createAddress(Long id, Long userId) {
        Address address = Address.of(
                createUser(userId),
                "detail",
                "zipcode",
                false
        );
        ReflectionTestUtils.setField(address, "id", id);
        return address;
    }

    public static AddressDto createAddressDto(Long id) {
        return AddressDto.of(
                id,
                UserDto.from(createUser()),
                "detail",
                "zipcode",
                false,
                now(),
                now()
        );
    }
}
