package com.example.product_service.controller;

import com.example.product_service.dto.OrderRequest;
import com.example.product_service.dto.ReserveResponse;
import com.example.product_service.entity.Product;
import com.example.product_service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping
    public Product create(@RequestBody Product product) {
        product.setReservedStock(0);
        return service.save(product);
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}/reduce")
    public Product reduceStock(@PathVariable Long id, @RequestParam int quantity) {
        return service.reduceStock(id, quantity);
    }

    @PostMapping("/reserve")
    public List<ReserveResponse> reserve(@RequestBody OrderRequest request) {
        return service.reserveStock(request);
    }

    @PostMapping("/confirm")
    public String confirm(@RequestBody OrderRequest request) {
        service.confirmStock(request);
        return "Stock confirmed";
    }

    @PostMapping("/release")
    public String release(@RequestBody OrderRequest request) {
        service.releaseStock(request);
        return "Stock released";
    }
}
