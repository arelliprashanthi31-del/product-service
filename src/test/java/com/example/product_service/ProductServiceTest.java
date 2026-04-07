package com.example.product_service;

import com.example.product_service.dto.Item;
import com.example.product_service.dto.OrderRequest;
import com.example.product_service.entity.Product;
import com.example.product_service.repository.ProductRepository;

import com.example.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductService service;

    // getById
    @Test
    void shouldReturnProduct() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(10);
        product.setReservedStock(0);

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.getById(1L);

        assertEquals("Laptop", result.getName());
    }

    //  product not found
    @Test
    void shouldThrowException_whenProductNotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.getById(1L));
    }

    //  reduce stock
    @Test
    void shouldReduceStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(500);
        product.setStock(10);
        product.setReservedStock(0);

        when(repo.findById(1L)).thenReturn(Optional.of(product));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Product result = service.reduceStock(1L, 2);

        assertEquals(8, result.getStock());
    }

    //  insufficient stock
    @Test
    void shouldThrowException_whenInsufficientStock() {
        Product product = new Product();
        product.setId(1L);
        product.setStock(2);
        product.setReservedStock(1); // important!

        Item item = new Item();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item));

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> service.reserveStock(request));
    }

    //  reserve stock
    @Test
    void shouldReserveStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(10);
        product.setReservedStock(0);

        Item item = new Item();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item));

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        var response = service.reserveStock(request);

        assertEquals(1, response.size());
        assertEquals(2, product.getReservedStock());
    }

    //  reserve stock insufficient
    @Test
    void shouldFailReserve_whenNotEnoughStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(2);
        product.setReservedStock(2);

        Item item = new Item();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item));

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> service.reserveStock(request));
    }

    // confirm stock
    @Test
    void shouldConfirmStock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(10);
        product.setReservedStock(2);

        Item item = new Item();
        item.setProductId(1L);
        item.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item));

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        service.confirmStock(request);

        assertEquals(8, product.getStock());
        assertEquals(0, product.getReservedStock());
    }

    //  release stock
    @Test
    void shouldReleaseStock() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(1000.0);
        product.setStock(10);
        product.setReservedStock(5);

        Item item = new Item();
        item.setProductId(1L);
        item.setQuantity(3);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(item));

        when(repo.findById(1L)).thenReturn(Optional.of(product));

        service.releaseStock(request);

        assertEquals(2, product.getReservedStock());
    }
}