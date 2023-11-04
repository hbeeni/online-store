package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUid(String uid);
    Optional<Admin> findByEmail(String email);
}
