package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.inventoryapp.dto.ProductRequest;
import com.chubb.inventoryapp.exception.CategoryNotFoundException;
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
    void updateProductProperties_Success() {
        Product product = new Product();
        product.setPrice(500.0f);
        
        ProductRequest request = new ProductRequest();
        request.setPrice(600.0f);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.updateProductProperties(1L, request);

        assertEquals(600.0f, product.getPrice());
        verify(productRepository).save(product);
    }
}