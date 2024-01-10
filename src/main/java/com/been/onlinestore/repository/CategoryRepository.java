package com.been.onlinestore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.been.onlinestore.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("select distinct c from Category c "
		+ "join c.products p "
		+ "where p.saleStatus = 'SALE' or p.saleStatus = 'OUT_OF_STOCK'")
	List<Category> findAllBySellingProducts();

	@Query("select c from Category c "
		+ "join fetch c.products p "
		+ "where c.id = :id")
	Optional<Category> findByIdWithAllProducts(@Param("id") Long id);

	Optional<Category> findByName(String name);
}
