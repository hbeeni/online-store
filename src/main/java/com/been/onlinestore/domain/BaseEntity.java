package com.been.onlinestore.domain;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity extends BaseTimeEntity {

	@CreatedBy
	@Column(nullable = false, updatable = false, length = 50)
	private String createdBy;

	@LastModifiedBy
	@Column(nullable = false, length = 50)
	private String modifiedBy;
}
