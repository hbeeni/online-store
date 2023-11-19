package com.been.onlinestore.controller.dto;

import lombok.Builder;

public record AddressRequest() {

    @Builder
    public record Create(
            String detail,
            String zipcode,
            Boolean defaultAddress
    ) {}

    @Builder
    public record Update(
            String detail,
            String zipcode,
            Boolean defaultAddress
    ) {}
}
