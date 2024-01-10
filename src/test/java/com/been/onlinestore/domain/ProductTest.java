package com.been.onlinestore.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.been.onlinestore.domain.constant.SaleStatus;

class ProductTest {

	@DisplayName("상품의 재고를 추가할 때 판매 상태가 OUT_OF_STOCK이면 SALE로 변경한다.")
	@Test
	void addStock() {
		//Given
		Product waitProduct = createProduct(SaleStatus.WAIT, 0);
		Product saleProduct = createProduct(SaleStatus.SALE, 0);
		Product outOfStockProduct = createProduct(SaleStatus.OUT_OF_STOCK, 0);
		Product closeProduct = createProduct(SaleStatus.CLOSE, 0);

		//When
		waitProduct.addStock(1);
		saleProduct.addStock(1);
		outOfStockProduct.addStock(1);
		closeProduct.addStock(1);

		//Then
		assertThat(waitProduct).hasFieldOrPropertyWithValue("saleStatus", SaleStatus.WAIT);
		assertThat(saleProduct).hasFieldOrPropertyWithValue("saleStatus", SaleStatus.SALE);
		assertThat(outOfStockProduct).hasFieldOrPropertyWithValue("saleStatus", SaleStatus.SALE);
		assertThat(closeProduct).hasFieldOrPropertyWithValue("saleStatus", SaleStatus.CLOSE);
	}

	@DisplayName("상품의 재고를 감소시킬 때 재고가 0이 되면, 판매 상태를 OUT_OF_STOCK으로 변경한다.")
	@Test
	void removeStock() {
		//Given
		int stockQuantity = 1;
		Product saleProduct = createProduct(SaleStatus.SALE, stockQuantity);

		//When
		saleProduct.removeStock(stockQuantity);

		//Then
		assertThat(saleProduct).hasFieldOrPropertyWithValue("saleStatus", SaleStatus.OUT_OF_STOCK);
	}

	@DisplayName("상품의 재고를 감소시킬 때 현재 재고보다 많은 재고를 감소 시킬 경우 예외를 발생한다.")
	@Test
	void removeStock_throwsIllegalArgumentException() {
		//Given
		int stockQuantity = 100;
		int removeStockQuantity = 200;
		Product saleProduct = createProduct(SaleStatus.SALE, stockQuantity);

		//When & Then
		assertThatThrownBy(() -> saleProduct.removeStock(removeStockQuantity))
			.isInstanceOf(IllegalArgumentException.class);
	}

	private static Product createProduct(SaleStatus saleStatus, int stockQuantity) {
		return Product.of(
			Category.of("category", "description"),
			"name",
			10000,
			"description",
			stockQuantity,
			0,
			saleStatus,
			3000,
			"image.png"
		);
	}
}
