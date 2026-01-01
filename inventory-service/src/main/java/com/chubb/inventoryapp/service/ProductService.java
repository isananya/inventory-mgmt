package com.chubb.inventoryapp.service;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.ProductRequest;
import com.chubb.inventoryapp.exception.CategoryNotFoundException;
import com.chubb.inventoryapp.exception.ProductAlreadyExistsException;
import com.chubb.inventoryapp.model.Category;
import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.repository.CategoryRepository;
import com.chubb.inventoryapp.repository.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
	
    public Long addProduct(ProductRequest request) {

        if (productRepository.existsByProductCode(request.getProductCode())) {
            throw new ProductAlreadyExistsException(request.getProductCode());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = new Product();
        product.setProductCode(request.getProductCode());
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setSpecifications(request.getSpecifications());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        productRepository.save(product);
        
        return product.getId();
    }
}
