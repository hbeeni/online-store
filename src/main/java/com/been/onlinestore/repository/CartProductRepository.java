package com.been.onlinestore.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.been.onlinestore.domain.CartProduct;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

	@Query("select cp from CartProduct cp "
		+ "join fetch cp.product p "
		+ "join fetch cp.user u "
		+ "where u.id = :userId and (p.saleStatus = 'SALE' or p.saleStatus = 'OUT_OF_STOCK')")
	List<CartProduct> findAllOnSaleOrOutOfStockByUserId(@Param("userId") Long userId);

	List<CartProduct> findAllByModifiedAtBefore(LocalDateTime modifiedAt);

	@Query("select cp from CartProduct cp "
		+ "join fetch cp.product p "
		+ "join cp.user u "
		+ "where u.id = :userId and p.id = :productId")
	Optional<CartProduct> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

	@Query("select cp from CartProduct cp "
		+ "join fetch cp.product p "
		+ "join fetch cp.user u "
		+ "where cp.id in :cartProductIds and u.id = :userId and p.saleStatus = 'SALE'")
	List<CartProduct> findAllOnSaleByIdInAndUserId(
		@Param("userId") Long userId, @Param("cartProductIds") List<Long> cartProductIds
	);

	@Query("select cp from CartProduct cp "
		+ "join fetch cp.product p "
		+ "join fetch cp.user u "
		+ "where cp.id = :cartProductId and u.id = :userId")
	Optional<CartProduct> findCartProduct(@Param("userId") Long userId, @Param("cartProductId") Long cartProductId);

	@Modifying
	@Query("delete from CartProduct cp where cp.id in :cartProductIds and cp.user.id = :userId")
	void deleteCartProducts(@Param("userId") Long userId, @Param("cartProductIds") Collection<Long> cartProductIds);
}
