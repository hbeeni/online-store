package com.been.onlinestore.service;

import static com.been.onlinestore.util.UserTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.request.UserServiceRequest;
import com.been.onlinestore.service.response.UserResponse;

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
				user.getPhone(),
				user.getRoleType()
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
				admin.getPhone(),
				admin.getRoleType()
		);
		given(userRepository.save(any())).willReturn(admin);

		//When
		Long id = sut.signUp(serviceRequest, serviceRequest.password());

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
		Optional<UserResponse> result = sut.searchUser(uid);

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
		Optional<UserResponse> result = sut.searchUser(uid);

		//Then
		assertThat(result).isEmpty();
		then(userRepository).should().findByUid(uid);
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

	@DisplayName("[어드민] 모든 회원 정보를 반환한다.")
	@Test
	void test_findUsers() {
		//Given
		Pageable pageable = PageRequest.of(0, 10);
		given(userRepository.findAll(pageable)).willReturn(Page.empty());

		//When
		Page<UserResponse> result = sut.findUsers(pageable);

		//Then
		assertThat(result).isNotNull();
		then(userRepository).should().findAll(pageable);
	}

	@DisplayName("[어드민] 회원의 상세 정보를 반환한다.")
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

	@DisplayName("[어드민] 존재하지 않는 회원의 정보를 조회하면, 예외를 던진다.")
	@Test
	void test_findUser_throwsEntityNotFoundException() {
		//Given
		long id = 1L;
		given(userRepository.findById(id)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.findUser(id))
				.isInstanceOf(EntityNotFoundException.class);
		then(userRepository).should().findById(id);
	}

	@Disabled("연관된 엔티티가 많아서 마지막에 구현")
	@DisplayName("[어드민] 일반 회원을 강제탈퇴 시킨 후, 삭제한 회원의 id를 반환한다.")
	@Test
	void test_deleteUser() {
		//Given
		long id = 1L;
		given(userRepository.existsByIdAndRoleType(id, RoleType.USER)).willReturn(true);

		//When
		Long result = sut.deleteUser(id);

		//Then
		assertThat(result).isNotNull();
		then(userRepository).should().existsByIdAndRoleType(id, RoleType.USER);
	}
}
