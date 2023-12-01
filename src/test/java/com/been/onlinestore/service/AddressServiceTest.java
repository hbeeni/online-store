package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Address;
import com.been.onlinestore.repository.AddressRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.request.AddressServiceRequest;
import com.been.onlinestore.service.response.AddressResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

import static com.been.onlinestore.util.AddressTestDataUtil.createAddress;
import static com.been.onlinestore.util.AddressTestDataUtil.createAddressServiceRequest;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 배송지")
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

	@Mock
	private AddressRepository addressRepository;
	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AddressService sut;

	@DisplayName("모든 배송지 정보를 반환한다.")
	@Test
	void test_findAddresses() {
		//Given
		long userId = 1L;
		given(addressRepository.findAllByUser_IdOrderByDefaultAddressDesc(userId)).willReturn(List.of(createAddress()));

		//When
		List<AddressResponse> result = sut.findAddresses(userId);

		//Then
		assertThat(result.size()).isEqualTo(1);
		then(addressRepository).should().findAllByUser_IdOrderByDefaultAddressDesc(userId);
	}

	@DisplayName("배송지를 조회하면, 배송지 정보를 반환한다.")
	@Test
	void test_findAddress() {
		//Given
		long addressId = 1L;
		long userId = 1L;
		given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(
				Optional.of(createAddress(addressId)));

		//When
		AddressResponse result = sut.findAddress(addressId, userId);

		//Then
		assertThat(result).isNotNull();
		then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
	}

	@DisplayName("배송지 조회시, 배송지가 없으면, 예외를 던진다.")
	@Test
	void test_findAddress_throwsEntityNotFoundException() {
		//Given
		long addressId = 1L;
		long userId = 1L;
		given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.findAddress(addressId, userId))
				.isInstanceOf(EntityNotFoundException.class);
		then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
	}

	@DisplayName("처음으로 배송지를 추가하면, 기본 배송지로 지정 후 저장된 배송지의 id를 반환한다.")
	@Test
	void test_addFirstAddress() {
		//Given
		long addressId = 1L;
		Long userId = 1L;

		AddressServiceRequest serviceRequest = createAddressServiceRequest("detail", "13232", false);

		given(addressRepository.findDefaultAddressByUserId(userId)).willReturn(Optional.empty());
		given(userRepository.getReferenceById(userId)).willReturn(createUser("user"));
		given(addressRepository.save(any())).willReturn(createAddress(addressId, true));

		//When
		Long result = sut.addAddress(userId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(addressId);
		then(addressRepository).should().findDefaultAddressByUserId(userId);
		then(userRepository).should().getReferenceById(userId);
		then(addressRepository).should().save(any());
	}

	@DisplayName("기본 배송지가 없을 때 기본 배송지로 지정해 추가하면, 기존의 기본 배송지는 해제한 후 저장된 배송지의 id를 반환한다.")
	@Test
	void test_addDefaultAddress_whenNotExistingDefaultAddress() {
		//Given
		Long userId = 1L;
		long defaultAddressId = 2L;
		Address defaultAddress = createAddress(defaultAddressId, true);

		AddressServiceRequest serviceRequest = createAddressServiceRequest("detail", "13232", false);

		given(addressRepository.findDefaultAddressByUserId(userId)).willReturn(Optional.empty());
		given(userRepository.getReferenceById(userId)).willReturn(createUser("user"));
		given(addressRepository.save(any())).willReturn(defaultAddress);

		//When
		Long result = sut.addAddress(userId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(defaultAddressId);

		then(addressRepository).should().findDefaultAddressByUserId(userId);
		then(userRepository).should().getReferenceById(userId);
		then(addressRepository).should().save(any());
	}

	@DisplayName("기본 배송지가 있을 때 기본 배송지로 지정해 추가하면, 기존의 기본 배송지는 해제한 후 저장된 배송지의 id를 반환한다.")
	@Test
	void test_addDefaultAddress_whenExistingDefaultAddress() {
		//Given
		Long userId = 1L;

		long existingDefaultAddressId = 1L;
		Address existingDefaultAddress = createAddress(existingDefaultAddressId, true);

		long newDefaultAddressId = 2L;
		Address newDefaultAddress = createAddress(newDefaultAddressId, true);

		AddressServiceRequest serviceRequest = createAddressServiceRequest("detail", "13232", true);

		given(addressRepository.findDefaultAddressByUserId(userId)).willReturn(Optional.of(existingDefaultAddress));
		given(userRepository.getReferenceById(userId)).willReturn(createUser("user"));
		given(addressRepository.save(any())).willReturn(newDefaultAddress);

		//When
		Long result = sut.addAddress(userId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(newDefaultAddressId);
		assertThat(existingDefaultAddress).hasFieldOrPropertyWithValue("defaultAddress", false);

		then(addressRepository).should().findDefaultAddressByUserId(userId);
		then(userRepository).should().getReferenceById(userId);
		then(addressRepository).should().save(any());
	}

	@DisplayName("배송지 정보를 수정하면, 수정된 배송지의 id를 반환한다.")
	@Test
	void test_updateAddress() {
		//Given
		long addressId = 1L;
		long userId = 1L;

		Address address = createAddress(addressId);
		AddressServiceRequest serviceRequest = createAddressServiceRequest(true);

		given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.of(address));

		//When
		Long result = sut.updateAddress(addressId, userId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(addressId);
		assertThat(address)
				.hasFieldOrPropertyWithValue("detail", serviceRequest.detail())
				.hasFieldOrPropertyWithValue("zipcode", serviceRequest.zipcode())
				.hasFieldOrPropertyWithValue("defaultAddress", serviceRequest.defaultAddress());
		then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
	}

	@DisplayName("없는 배송지를 수정하면, 예외를 던진다.")
	@Test
	void test_updateNonexistentAddress_throwsEntityNotFoundException() {
		//Given
		long addressId = 1L;
		long userId = 1L;

		AddressServiceRequest serviceRequest = createAddressServiceRequest(true);

		given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.updateAddress(addressId, userId, serviceRequest))
				.isInstanceOf(EntityNotFoundException.class);
		then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
	}

	@DisplayName("수정하려는 배송지의 회원과 요청한 회원이 다르면, 예외를 던진다.")
	@Test
	void test_updateOtherUserAddress_throwsEntityNotFoundException() {
		//Given
		long addressId = 1L;
		long requestUserId = 1L;

		AddressServiceRequest serviceRequest = createAddressServiceRequest(true);

		given(addressRepository.findByIdAndUser_Id(addressId, requestUserId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.updateAddress(addressId, requestUserId, serviceRequest))
				.isInstanceOf(EntityNotFoundException.class);
		then(addressRepository).should().findByIdAndUser_Id(addressId, requestUserId);
	}

	@DisplayName("기본 배송지가 없을 때, 현재 배송지를 기본 배송지로 수정하면, 수정된 배송지의 id를 반환한다.")
	@Test
	void test_updateToDefaultAddress_whenNotExistingDefaultAddress() {
		//Given
		long addressId = 1L;
		long userId = 1L;

		Address address = createAddress(addressId, false);
		AddressServiceRequest serviceRequest = createAddressServiceRequest(true);

		given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.of(address));
		given(addressRepository.findDefaultAddressByUserId(userId)).willReturn(Optional.empty());

		//When
		Long result = sut.updateAddress(addressId, userId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(addressId);
		assertThat(address.getDefaultAddress()).isTrue();
		then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
		then(addressRepository).should().findDefaultAddressByUserId(userId);
	}

	@DisplayName("기본 배송지가 있을 때, 현재 배송지를 기본 배송지로 수정하면, 원래 기본 배송지를 현재 기본 배송지로 바꾸고 수정된 배송지의 id를 반환한다.")
	@Test
	void test_updateToDefaultAddress_whenExistingDefaultAddress() {
		//Given
		Long userId = 1L;

		long existingDefaultAddressId = 1L;
		Address existingDefaultAddress = createAddress(existingDefaultAddressId, true);

		long newDefaultAddressId = 2L;
		Address newDefaultAddress = createAddress(newDefaultAddressId, false);

		AddressServiceRequest serviceRequest = createAddressServiceRequest(true);

		given(addressRepository.findByIdAndUser_Id(newDefaultAddressId, userId)).willReturn(
				Optional.of(newDefaultAddress));
		given(addressRepository.findDefaultAddressByUserId(userId)).willReturn(Optional.of(existingDefaultAddress));

		//When
		Long result = sut.updateAddress(newDefaultAddressId, userId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(newDefaultAddressId);
		assertThat(existingDefaultAddress.getDefaultAddress()).isFalse();
		assertThat(newDefaultAddress.getDefaultAddress()).isTrue();
		then(addressRepository).should().findByIdAndUser_Id(newDefaultAddressId, userId);
		then(addressRepository).should().findDefaultAddressByUserId(userId);
	}

	@DisplayName("기본 배송지가 아닌 배송지를 삭제하면, 삭제된 배송지의 id를 반환한다.")
	@Test
	void test_deleteNonDefaultAddress() {
		//Given
		long addressId = 1L;
		long userId = 1L;

		Address address = createAddress(addressId, false);

		given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.of(address));
		willDoNothing().given(addressRepository).deleteById(addressId);

		//When
		Long result = sut.deleteAddress(addressId, userId);

		//Then
		assertThat(result).isEqualTo(addressId);
		then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
		then(addressRepository).should().deleteById(addressId);
	}

	@DisplayName("기본 배송지를 삭제하면, 예외를 던진다.")
	@Test
	void test_deleteDefaultAddress_throwsIllegalArgumentException() {
		//Given
		long addressId = 1L;
		long userId = 1L;

		Address address = createAddress(addressId, true);

		given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.of(address));

		//When & Then
		assertThatThrownBy(() -> sut.deleteAddress(addressId, userId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(ErrorMessages.FAIL_TO_DELETE_DEFAULT_ADDRESS.getMessage());
		then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
	}
}
