package com.been.onlinestore.repository;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUid(String uid);
    boolean existsByEmail(String email);

    boolean existsByIdAndRoleType(Long id, RoleType roleType);

    Optional<User> findByUid(String uid);
}
