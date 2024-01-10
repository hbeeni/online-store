package com.been.onlinestore.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessages {

	DUPLICATE_ID("중복 ID 입니다."),
	ALREADY_SIGNED_UP_USER("이미 가입한 회원입니다."),
	ALREADY_EXISTING_CATEGORY("이미 존재하는 카테고리입니다."),

	NOT_FOUND_CATEGORY("존재하지 않는 카테고리입니다."),
	NOT_FOUND_USER("존재하지 않는 회원입니다."),
	NOT_FOUND_PRODUCT("존재하지 않는 상품입니다."),
	NOT_FOUND_ADDRESS("존재하지 않는 배송지 주소입니다."),
	NOT_FOUND_CART_PRODUCT("존재하지 않는 장바구니 상품입니다."),
	NOT_FOUND_ORDER("존재하지 않는 주문입니다."),
	NOT_FOUND_IMAGE("해당 이미지를 찾을 수 없습니다."),

	NOT_ENOUGH_STOCK("재고가 부족합니다."),

	FAIL_TO_UPDATE_PRODUCT("상품을 수정하는데 필요한 정보를 찾을 수 없습니다."),
	FAIL_TO_DELETE_DEFAULT_ADDRESS("먼저 다른 배송지를 기본 배송지로 변경해야 합니다."),

	CANNOT_ORDER_CART_PRODUCT_INCLUDED("주문할 수 없는 장바구니 상품이 포함되어 있습니다."),
	CANNOT_CANCEL_ORDER("결제 완료 상태의 주문만 취소할 수 있습니다."),
	CANNOT_START_PREPARING("결제 완료 상태의 주문만 상품 준비를 시작할 수 있습니다."),
	CANNOT_START_DELIVERY("상품 준비 중 상태의 주문만 배송을 시작할 수 있습니다."),
	CANNOT_COMPLETE_DELIVERY("배숭 중인 주문만 배송을 완료할 수 있습니다.");

	private final String message;
}
