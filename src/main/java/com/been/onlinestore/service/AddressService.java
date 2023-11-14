package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Address;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.dto.AddressDto;
import com.been.onlinestore.repository.AddressRepository;
import com.been.onlinestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressDto> findAddresses(Long userId) {
        return addressRepository.findAllByUser_Id(userId).stream()
                .map(AddressDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressDto findAddress(Long addressId, Long userId) {
        return addressRepository.findByIdAndUser_Id(addressId, userId)
                .map(AddressDto::from)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_FOUND_ADDRESS.getMessage()));
    }

    public Long addAddress(Long userId, AddressDto dto) {
        boolean defaultAddress;
        if (addressRepository.countByUser_Id(userId) == 0) {
            defaultAddress = true;
        } else {
            defaultAddress = dto.defaultAddress();
        }

        User user = userRepository.getReferenceById(userId);
        return addressRepository.save(dto.toEntity(user, defaultAddress)).getId();
    }

    public Long updateAddressInfo(Long addressId, Long userId, String updateDetail, String updateZipcode) {
        Address address = addressRepository.getReferenceById(addressId);
        User user = userRepository.getReferenceById(userId);

        String detail = address.getDetail();
        String zipcode = address.getZipcode();

        if (address.getUser().equals(user)) {
            if (updateDetail != null) {
                detail = updateDetail;
            }
            if (updateZipcode != null) {
                zipcode = updateZipcode;
            }
            address.updateInfo(detail, zipcode);
            return addressId;
        } else {
            throw new IllegalArgumentException(ErrorMessages.NOT_FOUND_ADDRESS.getMessage());
        }
    }

    public Long setDefaultAddress(Long addressId, Long userId) {
        Address updateDefaultAddress = addressRepository.getReferenceById(addressId);
        Optional<Address> originalDefaultAddress = addressRepository.findDefaultAddressByUserId(userId);
        User user = userRepository.getReferenceById(userId);

        if (updateDefaultAddress.getUser().equals(user)) {
            originalDefaultAddress.ifPresent(address -> address.updateDefaultAddress(false));
            updateDefaultAddress.updateDefaultAddress(true);
            return addressId;
        } else {
            throw new IllegalArgumentException(ErrorMessages.NOT_FOUND_ADDRESS.getMessage());
        }
    }

    public Long deleteAddress(Long addressId, Long userId) {
        addressRepository.deleteByIdAndUser_Id(addressId, userId);
        return addressId;
    }
}
