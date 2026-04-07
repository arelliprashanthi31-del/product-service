package com.example.product_service;

import com.example.product_service.controller.ProductController;
import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateProduct() throws Exception {

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(10);
        product.setReservedStock(0);

        when(service.save(product)).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetAllProducts() throws Exception {

        when(service.getAll()).thenReturn(List.of(new Product()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetProductById() throws Exception {

        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(500);
        product.setStock(10);
        product.setReservedStock(0);

        when(service.getById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReduceStock() throws Exception {

        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(500);
        product.setStock(8);
        product.setReservedStock(0);

        when(service.reduceStock(1L, 2)).thenReturn(product);

        mockMvc.perform(put("/products/1/reduce?quantity=2"))
                .andExpect(status().isOk());
    }
}