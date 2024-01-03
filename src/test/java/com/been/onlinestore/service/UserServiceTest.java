package com.been.onlinestore.service;

import static com.been.onlinestore.util.UserTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.request.UserServiceRequest;
import com.been.onlinestore.service.dto.response.UserResponse;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService sut;

	@DisplayName("일반 회원 - 회원 가입에 필요한 정보를 입력하면, 회원 가입을 시키고 해당 회원의 id(sequence)를 반환한다.")
	@Test
	void test_signUpUser() {
		//Given
		User user = createUser();
		UserServiceRequest.SignUp serviceRequest = UserServiceRequest.SignUp.of(
			user.getUid(),
			user.getPassword(),
			user.getName(),
			user.getEmail(),
			user.getNickname(),
			user.getPhone()
		);
		given(userRepository.save(any())).willReturn(user);

		//When
		Long id = sut.signUp(serviceRequest, serviceRequest.password());

		//Then
		assertThat(id).isEqualTo(user.getId());
		then(userRepository).should().save(any());
	}

	@DisplayName("어드민 회원 - 회원 가입에 필요한 정보를 입력하면, 회원 가입을 시키고 해당 회원의 id(sequence)를 반환한다.")
	@Test
	void test_signUpAdmin() {
		//Given
		User admin = createAdmin();
		UserServiceRequest.SignUp serviceRequest = UserServiceRequest.SignUp.of(
			admin.getUid(),
			admin.getPassword(),
			admin.getName(),
			admin.getEmail(),
			admin.getNickname(),
			admin.getPhone()
		);
		given(userRepository.save(any())).willReturn(admin);

		//When
		Long id = sut.signUp(serviceRequest, serviceRequest.password());

		//Then
		assertThat(id).isEqualTo(admin.getId());
		then(userRepository).should().save(any());
	}

	@DisplayName("회원의 상세 정보를 반환한다.")
	@Test
	void test_findUser() {
		//Given
		long id = 1L;
		given(userRepository.findById(id)).willReturn(Optional.of(createUser()));

		//When
		UserResponse result = sut.findUser(id);

		//Then
		assertThat(result).isNotNull();
		then(userRepository).should().findById(id);
	}

	@DisplayName("본인의 정보(닉네임, 휴대폰 번호)를 변경한 후, 변경한 회원의 id를 반환한다.")
	@Test
	void test_updateInfo() {
		//Given
		long id = 1L;
		given(userRepository.findById(id)).willReturn(Optional.of(createUser(id)));

		//When
		Long result = sut.updateInfo(id, "nickname", "01012341234");

		//Then
		assertThat(result).isNotNull();
		then(userRepository).should().findById(id);
	}
}
