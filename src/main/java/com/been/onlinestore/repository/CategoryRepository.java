package com.been.onlinestore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.been.onlinestore.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
