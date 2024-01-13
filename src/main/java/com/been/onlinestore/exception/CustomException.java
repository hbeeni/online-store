package com.been.onlinestore.exception;

import com.been.onlinestore.enums.ErrorMessages;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomException extends RuntimeException {

	private final String message;

	public CustomException(ErrorMessages errorMessages) {
		super(errorMessages.getMessage());
		message = errorMessages.getMessage();
	}
}
