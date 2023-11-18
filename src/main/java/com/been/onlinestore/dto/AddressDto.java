package com.been.onlinestore.dto;

import com.been.onlinestore.domain.Address;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.service.request.UserServiceRequest;

import java.time.LocalDateTime;

public record AddressDto(
        Long id,
        UserServiceRequest userServiceRequest,
        String detail,
        String zipcode,
        Boolean defaultAddress,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public static AddressDto of(UserServiceRequest userServiceRequest, String detail, String zipcode, Boolean defaultAddress) {
        return AddressDto.of(null, userServiceRequest, detail, zipcode, defaultAddress, null, null);
    }

    public static AddressDto of(Long id, UserServiceRequest userServiceRequest, String detail, String zipcode, Boolean defaultAddress, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new AddressDto(id, userServiceRequest, detail, zipcode, defaultAddress, createdAt, modifiedAt);
    }

    public static AddressDto from(Address entity) {
        return AddressDto.of(
                entity.getId(),
                null,
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
