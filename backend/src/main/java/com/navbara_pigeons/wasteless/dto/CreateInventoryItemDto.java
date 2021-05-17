package com.navbara_pigeons.wasteless.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateInventoryItemDto {

    private long productId;
    private long quantity;
    private Double pricePerItem;
    private Double totalPrice;
    private LocalDate manufactured;
    private LocalDate sellBy;
    private  LocalDate bestBefore;
    private LocalDate expires;

}