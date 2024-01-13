package com.been.onlinestore.service.admin;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.enums.ErrorMessages;
import com.been.onlinestore.exception.CustomException;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminUserService {

	private final UserRepository userRepository;

	public Optional<UserResponse> searchUser(String uid) {
		return userRepository.findByUid(uid).map(UserResponse::from);
	}

	public Page<UserResponse> findUsers(Pageable pageable) {
		return userRepository.findAll(pageable)
			.map(UserResponse::from);
	}

	public UserResponse findUser(Long id) {
		return userRepository.findById(id)
			.map(UserResponse::from)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_USER));
	}
}
