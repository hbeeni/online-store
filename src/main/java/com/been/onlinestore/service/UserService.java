package com.been.onlinestore.service;

import com.been.onlinestore.domain.Admin;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.repository.AdminRepository;
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
    private final AdminRepository adminRepository;

    public Long signUp(String uid, String password, String name, String email, String nickname, String phone, RoleType roleType) {
        if (roleType == null) {
            return signUpUser(uid, password, name, email, nickname, phone);
        } else {
            return signUpAdmin(uid, password, name, email, phone, roleType);
        }
    }

    private Long signUpUser(String uid, String password, String name, String email, String nickname, String phone) {
        validateDuplicateUserUid(uid);
        validateDuplicateUserEmail(email);
        return userRepository.save(User.of(uid, password, name, email, nickname, phone)).getId();
    }

    private Long signUpAdmin(String uid, String password, String name, String email, String phone, RoleType roleType) {
        validateDuplicateAdminUid(uid);
        validateDuplicateAdminEmail(email);
        return adminRepository.save(Admin.of(uid, password, name, email, phone, roleType)).getId();
    }

    private void validateDuplicateUserUid(String uid) {
        Optional<User> user = userRepository.findByUid(uid);
        if (user.isPresent()) {
            throw new IllegalStateException("중복 ID 입니다.");
        }
    }

    private void validateDuplicateUserEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new IllegalStateException("이미 가입한 회원입니다.");
        }
    }

    private void validateDuplicateAdminUid(String uid) {
        Optional<Admin> admin = adminRepository.findByUid(uid);
        if (admin.isPresent()) {
            throw new IllegalStateException("중복 ID 입니다.");
        }
    }

    private void validateDuplicateAdminEmail(String email) {
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            throw new IllegalStateException("이미 가입한 회원입니다.");
        }
    }
}
