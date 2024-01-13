package com.been.onlinestore.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.domain.User;
import com.been.onlinestore.enums.ErrorMessages;
import com.been.onlinestore.exception.CustomException;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.dto.request.UserServiceRequest;
import com.been.onlinestore.service.dto.response.UserResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

	private final UserRepository userRepository;

	public Long signUp(UserServiceRequest.SignUp serviceRequest, String encodedPassword) {
		if (userRepository.existsByUid(serviceRequest.uid())) {
			throw new CustomException(ErrorMessages.DUPLICATE_ID);
		}
		if (userRepository.existsByEmail(serviceRequest.email())) {
			throw new CustomException(ErrorMessages.ALREADY_SIGNED_UP_USER);
		}
		return userRepository.save(serviceRequest.toEntity(encodedPassword)).getId();
	}

	public UserResponse findUser(Long id) {
		return userRepository.findById(id)
			.map(UserResponse::from)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_USER));
	}

	public Long updateInfo(Long id, String nickname, String phone) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_USER));
		user.updateInfo(nickname, phone);
		return user.getId();
	}
}
