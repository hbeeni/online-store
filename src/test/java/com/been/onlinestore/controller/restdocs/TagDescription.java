package com.been.onlinestore.controller.restdocs;

public enum TagDescription {

    ADDRESS("배송지"),
    CART("장바구니"),
    CATEGORY("카테고리"),
    ORDER("주문"),
    PRODUCT("상품"),
    ORDER_PRODUCT("주문 상품"),
    USER("회원"),
    AUTH("회원가입/로그인");

    public final String tagName;

    TagDescription(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }
}
