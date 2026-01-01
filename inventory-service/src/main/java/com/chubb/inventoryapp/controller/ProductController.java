package com.chubb.inventoryapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.ProductRequest;
import com.chubb.inventoryapp.dto.ProductResponse;
import com.chubb.inventoryapp.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {
	private final ProductService productService;

	public ProductController(ProductService productService) {
		super();
		this.productService = productService;
	}
	
	@PostMapping
	public ResponseEntity<Long> addProduct(@Valid @RequestBody ProductRequest request) {
		Long productId = productService.addProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
	}
	
	@GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }
	
	@GetMapping("/{id}")
	public ProductResponse getProductById(@PathVariable Long id) {
		return productService.getProductById(id);
    }
	
	@GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Long categoryId) {
		List<ProductResponse> response = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("search/{name}")
    public ResponseEntity<List<ProductResponse>> getProductsByName(@PathVariable String name) {
		List<ProductResponse> response = productService.getProductsByName(name);
        return ResponseEntity.ok(response);
    }
}
