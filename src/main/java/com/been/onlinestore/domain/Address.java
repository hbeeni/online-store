package com.been.onlinestore.domain;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.been.onlinestore.domain.converter.DefaultAddressConverter;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@Entity
public class Address extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@Column(nullable = false, length = 50)
	private String detail;

	@Column(nullable = false, length = 20)
	private String zipcode;

	@Convert(converter = DefaultAddressConverter.class)
	@Column(columnDefinition = "char(1) not null default 'N'")
	private Boolean defaultAddress;

	protected Address() {
	}

	private Address(User user, String detail, String zipcode, Boolean defaultAddress) {
		this.user = user;
		this.detail = detail;
		this.zipcode = zipcode;
		this.defaultAddress = defaultAddress;
	}

	public static Address of(User user, String detail, String zipcode, Boolean defaultAddress) {
		return new Address(user, detail, zipcode, defaultAddress);
	}

	public void updateInfo(String detail, String zipcode, Boolean defaultAddress) {
		this.detail = detail;
		this.zipcode = zipcode;
		this.defaultAddress = defaultAddress;
	}

	public void updateDefaultAddress(boolean defaultAddress) {
		this.defaultAddress = defaultAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Address that)) {
			return false;
		}
		return this.getId() != null && Objects.equals(this.getId(), that.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId());
	}
}
