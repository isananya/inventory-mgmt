package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.chubb.inventoryapp.dto.ProductRequest;
import com.chubb.inventoryapp.dto.ProductResponse;
import com.chubb.inventoryapp.exception.CategoryNotFoundException;
import com.chubb.inventoryapp.exception.ProductAlreadyExistsException;
import com.chubb.inventoryapp.exception.ProductNotFoundException;
import com.chubb.inventoryapp.model.Category;
import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.repository.CategoryRepository;
import com.chubb.inventoryapp.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void addProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setProductCode("P001");
        request.setCategoryId(1L);
        request.setName("Laptop");
        request.setPrice(1000.0f);

        when(productRepository.existsByProductCode("P001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> {
            Product p = i.getArgument(0);
            p.setId(10L);
            return p;
        });

        Long id = productService.addProduct(request);
        assertEquals(10L, id);
    }

    @Test
    void addProduct_CategoryNotFound_ThrowsException() {
        ProductRequest request = new ProductRequest();
        request.setCategoryId(99L);
        request.setProductCode("P002");

        when(productRepository.existsByProductCode("P002")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.addProduct(request));
    }

    @Test
    void addProduct_ProductAlreadyExists_ThrowsException() {
        ProductRequest request = new ProductRequest();
        request.setProductCode("P001");

        when(productRepository.existsByProductCode("P001")).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.addProduct(request));
    }

    @Test
    void getAllProducts_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductResponse> result = productService.getAllProducts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
    }

    @Test
    void getProductById_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals("Test Product", response.getName());
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductsByCategory_Success() {
        Category category = new Category();
        category.setId(1L);
        Product product = new Product();
        product.setCategory(category);
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        Pageable pageable = PageRequest.of(0, 10);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByCategory(category, pageable)).thenReturn(page);

        Page<ProductResponse> result = productService.getProductsByCategory(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getProductsByCategory_CategoryNotFound_ThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.getProductsByCategory(1L, pageable));
    }

    @Test
    void getProductsByName_Success() {
        Product product = new Product();
        product.setName("Laptop Pro");
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findByNameContainingIgnoreCase("Laptop", pageable)).thenReturn(page);

        Page<ProductResponse> result = productService.getProductsByName("Laptop", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop Pro", result.getContent().get(0).getName());
    }

    @Test
    void updateProductProperties_Success() {
        Product product = new Product();
        product.setPrice(500.0f);
        
        ProductRequest request = new ProductRequest();
        request.setPrice(600.0f);
        request.setDescription("New Desc");
        request.setImageUrl("New Url");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateProductProperties(1L, request);

        assertEquals(600.0f, product.getPrice());
        assertEquals("New Desc", product.getDescription());
        assertEquals("New Url", product.getImageUrl());
        verify(productRepository).save(product);
    }

    @Test
    void updateProductProperties_ProductNotFound_ThrowsException() {
        ProductRequest request = new ProductRequest();
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProductProperties(1L, request));
    }
}