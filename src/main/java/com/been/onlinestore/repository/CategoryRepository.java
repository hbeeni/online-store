package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select distinct c from Category c left join fetch c.products p")
    List<Category> findAllWithProducts();

    @Query("select c from Category c left join fetch c.products p where c.id = :id")
    Optional<Category> findWithProductsById(@Param("id") Long id);
}
