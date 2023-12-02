package com.been.onlinestore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByUid(String uid);

	boolean existsByEmail(String email);

	boolean existsByIdAndRoleType(Long id, RoleType roleType);

	Optional<User> findByUid(String uid);
}
