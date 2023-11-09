package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Address;
import com.been.onlinestore.domain.User;

import java.time.LocalDateTime;

public record AddressDto(
        Long id,
        UserDto userDto,
        String detail,
        String zipcode,
        Boolean defaultAddress,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static AddressDto of(UserDto userDto, String detail, String zipcode, Boolean defaultAddress) {
        return AddressDto.of(null, userDto, detail, zipcode, defaultAddress, null, null);
    }

    public static AddressDto of(Long id, UserDto userDto, String detail, String zipcode, Boolean defaultAddress, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new AddressDto(id, userDto, detail, zipcode, defaultAddress, createdAt, modifiedAt);
    }

    public static AddressDto from(Address entity) {
        return AddressDto.of(
                entity.getId(),
                UserDto.from(entity.getUser()),
                entity.getDetail(),
                entity.getZipcode(),
                entity.getDefaultAddress(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }

    public Address toEntity(User user) {
        return Address.of(
                user,
                detail,
                zipcode,
                defaultAddress
        );
    }

    public Address toEntity(User user, boolean UpdateDefaultAddress) {
        return Address.of(
                user,
                detail,
                zipcode,
                UpdateDefaultAddress
        );
    }
}
