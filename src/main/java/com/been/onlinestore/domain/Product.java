package com.been.onlinestore.domain;

import com.been.onlinestore.domain.constant.SaleStatus;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Entity
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private User seller;


    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;

    private String description;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private int salesVolume;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'WAIT'")
    @Column(nullable = false, length = 20)
    private SaleStatus saleStatus;

    @Column(nullable = false)
    private int deliveryFee;

    @Column(length = 200)
    private String imageUrl;

    protected Product() {}

    private Product(Category category, User seller, String name, int price, String description, int stockQuantity, int salesVolume, SaleStatus saleStatus, int deliveryFee, String imageUrl) {
        this.category = category;
        this.seller = seller;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.salesVolume = salesVolume;
        this.saleStatus = saleStatus;
        this.deliveryFee = deliveryFee;
        this.imageUrl = imageUrl;
    }

    public static Product of(Category category, User seller, String name, int price, String description, int stockQuantity, int salesVolume, SaleStatus saleStatus, int deliveryFee, String imageUrl) {
        return new Product(category, seller, name, price, description, stockQuantity, salesVolume, saleStatus, deliveryFee, imageUrl);
    }

    public void updateInfo(Category category, String name, int price, String description, String imageUrl) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public void updateStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void updateSaleStatus(SaleStatus saleStatus) {
        this.saleStatus = saleStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
