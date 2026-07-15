package com.derocode.rag.services;


import com.derocode.rag.entities.Product;
import com.derocode.rag.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Optional<Product> getProductByName(String name){
        return productRepository.getProductByName(name);
    }
}
