package com.been.onlinestore.service;

import com.been.onlinestore.domain.Address;
import com.been.onlinestore.dto.AddressDto;
import com.been.onlinestore.repository.AddressRepository;
import com.been.onlinestore.repository.UserRepository;
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
import static com.been.onlinestore.util.AddressTestDataUtil.createAddressDto;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 주소")
@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock private AddressRepository addressRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private AddressService sut;

    @DisplayName("회원의 모든 주소 정보를 반환한다.")
    @Test
    void test_searchAddresses() {
        //Given
        long userId = 1L;
        given(addressRepository.findAllByUser_Id(userId)).willReturn(List.of(createAddress()));

        //When
        List<AddressDto> result = sut.findAddresses(userId);

        //Then
        assertThat(result.size()).isEqualTo(1);
        then(addressRepository).should().findAllByUser_Id(userId);
    }

    @DisplayName("회원의 주소를 조회하면, 주소 정보를 반환한다.")
    @Test
    void test_searchAddress() {
        //Given
        long addressId = 1L;
        long userId = 1L;
        given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.of(createAddress(addressId)));

        //When
        AddressDto result = sut.findAddress(addressId, userId);

        //Then
        assertThat(result).isNotNull();
        then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
    }

    @DisplayName("주소가 없으면, 예외를 던진다.")
    @Test
    void test_searchAddress_throwsIllegalArgumentException() {
        //Given
        long addressId = 1L;
        long userId = 1L;
        given(addressRepository.findByIdAndUser_Id(addressId, userId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findAddress(addressId, userId))
                .isInstanceOf(IllegalArgumentException.class);
        then(addressRepository).should().findByIdAndUser_Id(addressId, userId);
    }

    @DisplayName("주소를 추가하면, 저장된 주소의 id를 반환한다. 만약 주소 추가가 처음이면 기본 배송지로 지정한다.")
    @Test
    void test_addAddress() {
        //Given
        long addressId = 1L;
        Long userId = 1L;

        given(addressRepository.countByUser_Id(userId)).willReturn(0);
        given(userRepository.getReferenceById(userId)).willReturn(createUser("user"));
        given(addressRepository.save(any())).willReturn(createAddress(addressId, true));

        //When
        Long result = sut.addAddress(userId, createAddressDto(addressId));

        //Then
        assertThat(result).isEqualTo(addressId);
        then(addressRepository).should().countByUser_Id(userId);
        then(userRepository).should().getReferenceById(userId);
        then(addressRepository).should().save(any());
    }

    @DisplayName("주소 정보를 수정하면, 수정된 주소의 id를 반환한다.")
    @Test
    void test_updateAddressInfo() {
        //Given
        long addressId = 1L;
        long userId = 1L;

        Address address = createAddress(addressId);
        String detail = "update detail";
        String zipcode = "update zipcode";

        given(addressRepository.getReferenceById(addressId)).willReturn(address);
        given(userRepository.getReferenceById(userId)).willReturn(createUser("user"));

        //When
        Long result = sut.updateAddressInfo(addressId, userId, detail, zipcode);

        //Then
        assertThat(result).isEqualTo(addressId);
        assertThat(address)
                .hasFieldOrPropertyWithValue("detail", detail)
                .hasFieldOrPropertyWithValue("zipcode", zipcode);
        then(addressRepository).should().getReferenceById(addressId);
        then(userRepository).should().getReferenceById(userId);
    }

    @DisplayName("없는 주소를 수정하면, 예외를 던진다.")
    @Test
    void test_updateAddressInfo_throwsEntityNotFoundException() {
        //Given
        long addressId = 1L;
        long userId = 1L;

        String detail = "update detail";
        String zipcode = "update zipcode";

        given(addressRepository.getReferenceById(addressId)).willThrow(EntityNotFoundException.class);

        //When & Then
        assertThatThrownBy(() -> sut.updateAddressInfo(addressId, userId, detail, zipcode))
                .isInstanceOf(EntityNotFoundException.class);
        then(addressRepository).should().getReferenceById(addressId);
        then(userRepository).shouldHaveNoInteractions();
    }

    @DisplayName("수정하려는 주소의 회원과 요청한 회원이 다르면, 예외를 던진다.")
    @Test
    void test_updateAddress_throwsIllegalArgumentException() {
        //Given
        long addressId = 1L;
        long addressUserId = 1L;
        long requestUserId = 2L;

        Address address = createAddress(addressId, addressUserId);
        String detail = "update detail";
        String zipcode = "update zipcode";

        given(addressRepository.getReferenceById(addressId)).willReturn(address);
        given(userRepository.getReferenceById(requestUserId)).willReturn(createUser(requestUserId));

        //When & Then
        assertThatThrownBy(() -> sut.updateAddressInfo(addressId, requestUserId, detail, zipcode))
                .isInstanceOf(IllegalArgumentException.class);
        then(addressRepository).should().getReferenceById(addressId);
        then(userRepository).should().getReferenceById(requestUserId);
    }

    @DisplayName("현재 주소를 기본 배송지로 수정하면, 원래 기본 배송지를 현재 기본 배송지로 바꾸고 수정된 주소의 id를 반환한다.")
    @Test
    void test_setDefaultAddress() {
        //Given
        long addressId = 2L;
        long userId = 1L;

        Address originalDefaultAddress = createAddress(1L, true);
        Address updateDefaultAddress = createAddress(addressId, false);

        given(addressRepository.getReferenceById(addressId)).willReturn(updateDefaultAddress);
        given(addressRepository.findDefaultAddressByUserId(userId)).willReturn(Optional.of(originalDefaultAddress));
        given(userRepository.getReferenceById(userId)).willReturn(createUser("user"));

        //When
        Long result = sut.setDefaultAddress(addressId, userId);

        //Then
        assertThat(result).isEqualTo(addressId);
        assertThat(originalDefaultAddress.getDefaultAddress()).isFalse();
        assertThat(updateDefaultAddress.getDefaultAddress()).isTrue();
        then(addressRepository).should().getReferenceById(addressId);
        then(addressRepository).should().findDefaultAddressByUserId(userId);
        then(userRepository).should().getReferenceById(userId);
    }

    @DisplayName("주소를 삭제하면, 삭제된 주소의 id를 반환한다.")
    @Test
    void test_deleteAddress() {
        //Given
        long addressId = 1L;
        long userId = 1L;
        willDoNothing().given(addressRepository).deleteByIdAndUser_Id(addressId, userId);

        //When
        Long result = sut.deleteAddress(addressId, userId);

        //Then
        assertThat(result).isEqualTo(addressId);
        then(addressRepository).should().deleteByIdAndUser_Id(addressId, userId);
    }
}
