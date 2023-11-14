package com.been.onlinestore.common;

public enum ErrorMessages {

    DUPLICATE_ID("중복 ID 입니다."),
    ALREADY_SIGNED_UP_USER("이미 가입한 회원입니다."),

    NOT_FOUND_CATEGORY("존재하지 않는 카테고리입니다."),
    NOT_FOUND_USER("존재하지 않는 회원입니다."),
    NOT_FOUND_PRODUCT("존재하지 않는 상품입니다."),
    NOT_FOUND_ADDRESS("존재하지 않는 주소입니다."),
    NOT_FOUND_CART("존재하지 않는 장바구니입니다."),
    NOT_FOUND_CART_PRODUCT("존재하지 않는 장바구니 상품입니다."),
    NOT_FOUND_CART_PRODUCT_IN_CART("장바구니에 해당 상품이 존재하지 않습니다."),
    NOT_FOUND_ORDER("존재하지 않는 주문입니다."),

    NOT_MATCH_ORDER_PRODUCT_COUNT_TO_QUANTITY_COUNT("입력된 주문 상품의 수와 수량의 수가 맞지 않습니다."),
    NOT_SALE_PRODUCT("판매하지 않는 상품을 주문하였습니다."),
    NOT_ENOUGH_STOCK("재고가 부족합니다."),

    FAIL_TO_UPDATE_CATEGORY("카테고리 수정 실패. 카테고리를 수정하는데 필요한 정보를 찾을 수 없습니다."),
    FAIL_TO_UPDATE_PRODUCT("상품 수정 실패. 상품을 수정하는데 필요한 정보를 찾을 수 없습니다."),

    CANNOT_PREPARING("상품을 준비할 수 없습니다. 결제 완료 단계에서만 상품 준비중 단계로 넘어갈 수 있습니다."),
    CANNOT_DELIVERING("배송을 시작할 수 없습니다. 상품 준비 중 단계에서만 배송 중 단계로 넘어갈 수 있습니다."),
    CANNOT_FINAL_DELIVERY("배송을 완료할 수 없습니다. 배송 중 단계에서만 배송 완료 단계로 넘어갈 수 있습니다."),
    CANNOT_CANCEL_ORDER_PRODUCT("결제 완료 상태인 상품만 취소할 수 있습니다.");


    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
