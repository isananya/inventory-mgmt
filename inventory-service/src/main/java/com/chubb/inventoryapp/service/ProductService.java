package com.chubb.inventoryapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.ProductRequest;
import com.chubb.inventoryapp.dto.ProductResponse;
import com.chubb.inventoryapp.exception.CategoryNotFoundException;
import com.chubb.inventoryapp.exception.ProductAlreadyExistsException;
import com.chubb.inventoryapp.exception.ProductNotFoundException;
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
    
    private ProductResponse mapToResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getProductCode(),
                p.getName(),
                p.getBrand(),
                p.getPrice(),
                p.getDescription(),
                p.getSpecifications(),
                p.getImageUrl(),
                p.getCategory()
        );
    }
    
    public Page<ProductResponse> getAllProducts(Pageable pageable){
    	return productRepository.findAll(pageable)
                .map(this::mapToResponse);    }
    
    public ProductResponse getProductById(Long id) {
    	Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException());
    	return mapToResponse(product);
    }
    
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        
        return productRepository.findByCategory(category, pageable)
                .map(this::mapToResponse);
        }
    
    public Page<ProductResponse> getProductsByName(String name, Pageable pageable) {

        return productRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(this::mapToResponse);
        }
    
}
