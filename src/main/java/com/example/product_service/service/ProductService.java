package com.example.product_service.service;

import com.example.product_service.dto.Item;
import com.example.product_service.dto.OrderRequest;
import com.example.product_service.dto.ReserveResponse;
import com.example.product_service.entity.Product;
import com.example.product_service.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public Product save(Product product) {
        return repo.save(product);
    }

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Product getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public Product reduceStock(Long id, int quantity) {

        Product product = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setStock(product.getStock() - quantity);

        return repo.save(product);
    }

    public List<ReserveResponse> reserveStock(OrderRequest request) {

        List<ReserveResponse> responseList = new ArrayList<>();
        for (Item item : request.getItems()) {

            Product product = repo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            int available = product.getStock() - product.getReservedStock();

            if (available < item.getQuantity()) {
                throw new RuntimeException("Not enough stock");
            }

            product.setReservedStock(
                    product.getReservedStock() + item.getQuantity()
            );

             repo.save(product);
            ReserveResponse response = new ReserveResponse();
            response.setProductId(product.getId());
            response.setReservedQuantity(item.getQuantity());
            response.setAvailableStock(
                    product.getStock() - product.getReservedStock()
            );

            responseList.add(response);
        }
        return responseList;

    }

    public void confirmStock(OrderRequest request) {

        for (Item item : request.getItems()) {

            Product product = repo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setStock(product.getStock() - item.getQuantity());

            product.setReservedStock(
                    product.getReservedStock() - item.getQuantity()
            );

            repo.save(product);
        }
    }

    public void releaseStock(OrderRequest request) {

        for (Item item : request.getItems()) {

            Product product = repo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setReservedStock(
                    product.getReservedStock() - item.getQuantity()
            );

            // safety check
            if (product.getReservedStock() < 0) {
                product.setReservedStock(0);
            }

            repo.save(product);
        }
    }
}
