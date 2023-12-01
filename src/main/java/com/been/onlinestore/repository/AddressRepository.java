package com.been.onlinestore.repository;

import com.been.onlinestore.domain.Address;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

	List<Address> findAllByUser_IdOrderByDefaultAddressDesc(Long userId);

	Optional<Address> findByIdAndUser_Id(Long addressId, Long userId);

	@Query("select a from Address a where a.defaultAddress = true and a.user.id = :userId")
	Optional<Address> findDefaultAddressByUserId(@Param("userId") Long userId);
}
