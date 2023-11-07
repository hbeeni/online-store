package com.been.onlinestore.service;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.dto.UserDto;
import com.been.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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
        User admin = createAdmin();
        given(userRepository.save(any())).willReturn(admin);

        //When
        Long id = sut.signUp(
                admin.getUid(),
                admin.getPassword(),
                admin.getName(),
                admin.getEmail(),
                admin.getNickname(),
                admin.getPhone(),
                admin.getRoleType()
        );

        //Then
        assertThat(id).isEqualTo(admin.getId());
        then(userRepository).should().save(any());
    }

    @DisplayName("존재하는 회원 ID를 검색하면, 회원 데이터를 Optional로 반환한다.")
    @Test
    void test_searchExistentUser() {
        //Given
        String uid = "user";
        given(userRepository.findByUid(uid)).willReturn(Optional.of(createUser(uid)));

        //When
        Optional<UserDto> result = sut.searchUser(uid);

        //Then
        assertThat(result).isPresent();
        then(userRepository).should().findByUid(uid);
    }

    @DisplayName("존재하지 않는 회원 ID를 검색하면, 비어있는 Optional을 반환한다.")
    @Test
    void test_searchNonExistentUser() {
        //Given
        String uid = "wrong-user";
        given(userRepository.findByUid(uid)).willReturn(Optional.empty());

        //When
        Optional<UserDto> result = sut.searchUser(uid);

        //Then
        assertThat(result).isEmpty();
        then(userRepository).should().findByUid(uid);
    }
}
