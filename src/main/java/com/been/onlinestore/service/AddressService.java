package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Address;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.AddressRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.request.AddressServiceRequest;
import com.been.onlinestore.service.response.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> findAddresses(Long userId) {
        return addressRepository.findAllByUser_Id(userId).stream()
                .map(AddressResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AddressResponse findAddress(Long addressId, Long userId) {
        return addressRepository.findByIdAndUser_Id(addressId, userId)
                .map(AddressResponse::from)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ADDRESS.getMessage()));
    }

    public Long addAddress(Long userId, AddressServiceRequest.Create serviceRequest) {
        Optional<Address> originalDefaultAddressOptional = addressRepository.findDefaultAddressByUserId(userId);
        boolean defaultAddress = serviceRequest.defaultAddress();

        if (originalDefaultAddressOptional.isEmpty()) {
            defaultAddress = true;
        } else {
            if (defaultAddress) {
                Address originalDefaultAddress = originalDefaultAddressOptional.get();
                originalDefaultAddress.updateDefaultAddress(false);
            }
        }

        User user = userRepository.getReferenceById(userId);
        return addressRepository.save(serviceRequest.toEntity(user, defaultAddress)).getId();
    }

    public Long updateAddress(Long addressId, Long userId, AddressServiceRequest.Update serviceRequest) {
        Address address = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ADDRESS.getMessage()));

        if (serviceRequest.defaultAddress()) {
            addressRepository.findDefaultAddressByUserId(userId)
                    .ifPresent(a -> a.updateDefaultAddress(false));
        }

        address.updateInfo(serviceRequest.detail(), serviceRequest.zipcode(), serviceRequest.defaultAddress());
        return addressId;
    }

    public Long deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findByIdAndUser_Id(addressId, userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_ADDRESS.getMessage()));

        if (address.getDefaultAddress()) {
            throw new IllegalArgumentException(ErrorMessages.FAIL_TO_DELETE_DEFAULT_ADDRESS.getMessage());
        }

        addressRepository.deleteById(addressId);
        return addressId;
    }
}
