package com.example.product_service.dto;

import lombok.Data;

import java.util.List;

    @Data
    public class OrderRequest {
        private List<Item> items;
    }

