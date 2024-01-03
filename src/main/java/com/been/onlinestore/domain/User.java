package com.been.onlinestore.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

import com.been.onlinestore.domain.constant.RoleType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

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

	@Column(length = 20)
	private String nickname;

	@Column(nullable = false, length = 20)
	private String phone;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'USER'")
	@Column(nullable = false, length = 20)
	private RoleType roleType;

	protected User() {
	}

	private User(
		String uid, String password, String name, String email, String nickname, String phone, RoleType roleType
	) {
		this.uid = uid;
		this.password = password;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.phone = phone;
		this.roleType = roleType != null ? roleType : RoleType.USER;
	}

	public static User of(
		String uid, String password, String name, String email, String nickname, String phone, RoleType roleType
	) {
		return new User(uid, password, name, email, nickname, phone, roleType);
	}

	public void updateInfo(String nickname, String phone) {
		this.nickname = nickname;
		this.phone = phone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof User that)) {
			return false;
		}
		return this.getId() != null && Objects.equals(this.getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}
}
