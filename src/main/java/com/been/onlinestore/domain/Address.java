package com.been.onlinestore.domain;

import java.time.LocalDateTime;

public class Address {

    private Long id;

    private User user;

    private String detail;
    private String zipcode;
    private boolean default_address;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
