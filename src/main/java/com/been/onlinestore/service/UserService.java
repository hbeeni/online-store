package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.dto.UserDto;
import com.been.onlinestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    public Long signUp(String uid, String password, String name, String email, String nickname, String phone, RoleType roleType) {
        validateDuplicateUid(uid);
        validateDuplicateEmail(email);
        if (roleType == null) {
            return userRepository.save(User.of(uid, password, name, email, nickname, phone)).getId();
        } else {
            return userRepository.save(User.of(uid, password, name, email, nickname, phone, roleType)).getId();
        }
    }

    public Optional<UserDto> searchUser(String uid) {
        return userRepository.findByUid(uid).map(UserDto::from);
    }

    private void validateDuplicateUid(String uid) {
        userRepository.findByUid(uid)
                .ifPresent(user -> {
                    throw new IllegalStateException(ErrorMessages.DUPLICATE_ID.getMessage());
                });
    }

    private void validateDuplicateEmail(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new IllegalStateException(ErrorMessages.ALREADY_SIGNED_UP_USER.getMessage());
                });
    }
}
