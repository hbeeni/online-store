package com.been.onlinestore.service.admin;

import static com.been.onlinestore.util.UserTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.response.UserResponse;

@DisplayName("어드민 비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AdminUserService sut;

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

	@DisplayName("모든 회원 정보를 반환한다.")
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

	@DisplayName("존재하지 않는 회원의 정보를 조회하면, 예외를 던진다.")
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
}
