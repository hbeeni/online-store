package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.repository.querydsl.product.ProductRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

	@Modifying
	@Query("update Product p set p.category = null where p.category.id = :categoryId")
	int bulkCategoryNull(@Param("categoryId") Long categoryId);

	@Query("select p from Product p where p.saleStatus = 'SALE' and p.category.id = :categoryId")
	Page<Product> findAllOnSaleByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

	@Query(value = "select p from Product p join fetch p.category where p.saleStatus = 'SALE'",
			countQuery = "select count(p) from Product p where p.saleStatus = 'SALE'")
	Page<Product> findAllOnSale(Pageable pageable);

	@Query(value = "select p from Product p join fetch p.category where p.saleStatus = 'SALE' and p.name like concat('%', :name, '%')",
			countQuery = "select count(p) from Product p where p.saleStatus = 'SALE' and p.name like concat('%', :name, '%')")
	Page<Product> findAllOnSaleByName(@Param("name") String name, Pageable pageable);

	@Query("select p from Product p where p.saleStatus = 'SALE' and p.id in :ids")
	List<Product> findAllOnSaleById(@Param("ids") Collection<Long> ids);

	@Query("select p from Product p join fetch p.category where p.saleStatus = 'SALE' and p.id = :id")
	Optional<Product> findOnSaleById(@Param("id") Long id);

	Optional<Product> findByIdAndSeller_Id(Long productId, Long sellerId);
}
