package com.been.onlinestore.service.request;

import com.been.onlinestore.domain.Address;
import com.been.onlinestore.domain.User;

public record AddressServiceRequest() {

    public record Create(
            String detail,
            String zipcode,
            Boolean defaultAddress
    ) {

        public static Create of(String detail, String zipcode, Boolean defaultAddress) {
            return new Create(detail, zipcode, defaultAddress);
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

    public record Update(
            String detail,
            String zipcode,
            Boolean defaultAddress
    ) {

        public static Update of(String detail, String zipcode, Boolean defaultAddress) {
            return new Update(detail, zipcode, defaultAddress);
        }
    }
}
