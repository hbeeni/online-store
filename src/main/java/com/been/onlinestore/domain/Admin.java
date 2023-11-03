package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.RoleType;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Entity
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true, length = 50)
    private String uid; //로그인 ID

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType roleType;

    protected Admin() {}

    private Admin(String uid, String password, String name, String email, String phone, RoleType roleType) {
        this.uid = uid;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.roleType = roleType;
    }

    public static Admin of(String uid, String password, String name, String email, String phone, RoleType roleType) {
        return new Admin(uid, password, name, email, phone, roleType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
