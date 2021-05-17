package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.BasicInventoryDto;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "INVENTORY")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @OneToOne()
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(name = "QUANTITY")
    private long quantity;

    @Column(name = "PRICE_PER_ITEM")
    private Double pricePerItem;

    @Column(name = "TOTAL_PRICE")
    private Double totalPrice;

    @Column(name = "EXPIRES")
    private ZonedDateTime expires;

    @Column(name = "MANUFACTURED")
    private ZonedDateTime manufactured;

    @Column(name = "SELL_BY")
    private ZonedDateTime sellBy;

    @Column(name = "BEST_BEFORE")
    private ZonedDateTime bestBefore;

    public Inventory(BasicInventoryDto inventory) {
        this.id = inventory.getId();
        this.product = new Product(inventory.getProduct());
        this.quantity = inventory.getQuantity();
        this.pricePerItem = inventory.getPricePerItem();
        this.totalPrice = inventory.getTotalPrice();
        this.expires = inventory.getExpires();
        this.manufactured = inventory.getManufactured();
        this.sellBy = inventory.getSellBy();
        this.bestBefore = inventory.getBestBefore();
    }

    public Inventory() {

    }

}
