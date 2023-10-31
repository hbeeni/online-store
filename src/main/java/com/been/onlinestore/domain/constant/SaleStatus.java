package com.been.onlinestore.domain.constant;

public enum SaleStatus {

    WAIT("판매 대기"),
    SALE("판매 중"),
    OUT_OF_STOCK("품절"),
    CLOSE("판매 종료");

    private final String description;

    SaleStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
