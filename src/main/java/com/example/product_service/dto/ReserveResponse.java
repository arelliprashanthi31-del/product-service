package com.example.product_service.dto;

import lombok.Data;

@Data
public class ReserveResponse {

    private Long productId;
    private int reservedQuantity;
    private int availableStock;

}
