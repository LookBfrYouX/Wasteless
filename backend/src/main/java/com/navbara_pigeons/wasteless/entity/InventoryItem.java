package com.navbara_pigeons.wasteless.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "INVENTORY")
public class InventoryItem {

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
    private LocalDate expires;

    @Column(name = "MANUFACTURED")
    private LocalDate manufactured;

    @Column(name = "SELL_BY")
    private LocalDate sellBy;

    @Column(name = "BEST_BEFORE")
    private LocalDate bestBefore;
}
