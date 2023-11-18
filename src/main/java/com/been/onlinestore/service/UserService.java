package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.request.UserServiceRequest;
import com.been.onlinestore.service.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public Long signUp(UserServiceRequest.SignUp serviceRequest, String encodedPassword) {
        if (userRepository.existsByUid(serviceRequest.uid())) {
            throw new IllegalStateException(ErrorMessages.DUPLICATE_ID.getMessage());
        }
        if (userRepository.existsByEmail(serviceRequest.email())) {
            throw new IllegalStateException(ErrorMessages.ALREADY_SIGNED_UP_USER.getMessage());
        }
        return userRepository.save(serviceRequest.toEntity(encodedPassword)).getId();
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> searchUser(String uid) {
        return userRepository.findByUid(uid).map(UserResponse::from);
    }

    public Long updateInfo(Long id, String nickname, String phone) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_USER.getMessage()));
        user.updateInfo(nickname, phone);
        return user.getId();
    }

    public Page<UserResponse> findUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::from);
    }

    public UserResponse findUser(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_USER.getMessage()));
    }

    public Long deleteUser(Long id) {
        return null;
    }
}
