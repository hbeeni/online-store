package com.been.onlinestore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.been.onlinestore.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Query("select distinct c from Category c "
		+ "join c.products p "
		+ "where p.saleStatus = 'SALE'")
	List<Category> findAllByProductOnSale();
}
