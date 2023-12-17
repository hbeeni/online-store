package com.been.onlinestore.controller.restdocs;

public enum FieldDescription {

	ADD("추가된 "),
	UPDATE("수정된 "),
	DELETE("삭제된 "),

	CREATED_AT("생성일"),
	CREATED_BY("생성자"),
	MODIFIED_AT("수정일"),
	MODIFIED_BY("수정자"),

	ADDRESS_ID("배송지 시퀀스"),
	ADDRESS_DETAIL("배송지 주소"),
	ADDRESS_ZIPCODE("우편번호"),
	ADDRESS_DEFAULT_ADDRESS("기본 배송지 여부 (Y, N)"),

	PRODUCT_ID("상품 시퀀스"),
	PRODUCT_NAME("상품명"),
	PRODUCT_PRICE("상품 가격"),
	PRODUCT_DESCRIPTION("상품 설명"),
	PRODUCT_STOCK_QUANTITY("상품 재고 수량"),
	PRODUCT_SALES_VOLUME("상품 판매량"),
	PRODUCT_SALE_STATUS("상품 판매 상태 (WAIT, SALE, OUT_OF_STOCK, CLOSE)"),
	PRODUCT_DELIVERY_FEE("상품 배송비"),
	PRODUCT_IMAGE("상품 이미지 파일"),
	PRODUCT_IMAGE_URL("상품 이미지 URL"),

	CATEGORY_ID("카테고리 시퀀스"),
	CATEGORY_NAME("카테고리명"),
	CATEGORY_DESCRIPTION("카테고리 설명"),
	CATEGORY_PRODUCT_COUNT("해당 카테고리에 속한 상품 수"),

	CART_ID("장바구니 시퀀스"),
	CART_TOTAL_PRICE("장바구니 상품 가격 총합"),
	CART_PRODUCT_ID("장바구니 상품 시퀀스"),
	CART_PRODUCT_QUANTITY("장바구니에 담은 상품 수량"),
	CART_PRODUCT_TOTAL_PRICE("장바구니에 담은 상품 가격 총합"),

	ORDER_ID("주문 시퀀스"),
	ORDER_TOTAL_PRICE("주문 총합"),
	ORDER_STATUS("주문 상태 (ORDER, CANCEL)"),
	ORDER_DELIVERY_STATUS("배송 상태 (ACCEPT, PREPARING, DELIVERING, FINAL_DELIVERY)"),
	DELIVERY_FEE("배송비"),
	DELIVERED_AT("배송 완료일"),

	ORDERER_ID("주문자 시퀀스"),
	ORDERER_UID("주문자 아이디"),
	ORDERER_PHONE("주문자 휴대폰 번호"),

	DELIVERY_REQUEST_ADDRESS("배송지 주소"),
	DELIVERY_REQUEST_RECEIVER_NAME("수령인 이름"),
	DELIVERY_REQUEST_RECEIVER_PHONE("수령인 휴대폰 번호"),

	ORDER_PRODUCT_ID("주문 상품 시퀀스"),
	ORDER_PRODUCT_NAME("주문 상품 이름"),
	ORDER_PRODUCT_PRICE("주문 상품 가격"),
	ORDER_PRODUCT_QUANTITY("주문 상품 수량"),
	ORDER_PRODUCT_TOTAL_PRICE("주문 상품 총합"),

	USER_ID("회원 시퀀스"),
	USER_UID("회원 아이디"),
	USER_PASSWORD("회원 비밀번호"),
	USER_NAME("회원 이름"),
	USER_EMAIL("회원 이메일"),
	USER_NICKNAME("회원 닉네임"),
	USER_PHONE("회원 휴대폰 번호"),
	USER_ROLE_TYPE("회원 권한 (USER, ADMIN)"),
	USER_CREATED_AT("회원 가입 날짜"),
	USER_MODIFIED_AT("최근 회원 정보 수정 날짜");

	private final String description;

	FieldDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
