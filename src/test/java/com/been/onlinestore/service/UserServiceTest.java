package com.been.onlinestore.service;

import com.been.onlinestore.domain.Admin;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.AdminRepository;
import com.been.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.been.onlinestore.util.UserTestDataUtil.createAdmin;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AdminRepository adminRepository;

    @InjectMocks private UserService sut;

    @DisplayName("일반 회원 - 회원 가입에 필요한 정보를 입력하면, 회원 가입을 시키고 해당 회원의 id(sequence)를 반환한다.")
    @Test
    void test_signUpUser() {
        //Given
        User user = createUser();
        given(userRepository.save(any())).willReturn(user);

        //When
        Long id = sut.signUp(
                user.getUid(),
                user.getPassword(),
                user.getName(),
                user.getEmail(),
                user.getNickname(),
                user.getPhone(),
                null
        );

        //Then
        assertThat(id).isEqualTo(user.getId());
        then(userRepository).should().save(any());
    }

    @DisplayName("어드민 회원 - 회원 가입에 필요한 정보를 입력하면, 회원 가입을 시키고 해당 회원의 id(sequence)를 반환한다.")
    @Test
    void test_signUpAdmin() {
        //Given
        Admin admin = createAdmin();
        given(adminRepository.save(any())).willReturn(admin);

        //When
        Long id = sut.signUp(
                admin.getUid(),
                admin.getPassword(),
                admin.getName(),
                admin.getEmail(),
                null,
                admin.getPhone(),
                admin.getRoleType()
        );

        //Then
        assertThat(id).isEqualTo(admin.getId());
        then(adminRepository).should().save(any());
    }
}
