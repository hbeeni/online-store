package com.been.onlinestore.domain;

import java.time.LocalDateTime;

public class User {

    private Long id;

    private String uid;
    private String password;
    private String name;
    private String email;
    private String nickname;
    private String phone;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
