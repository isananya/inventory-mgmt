package com.chubb.inventoryapp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
    		@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "name") String sortBy){
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		Page<ProductResponse> response = productService.getAllProducts(pageable);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
		return ResponseEntity.ok(productService.getProductById(id));
    }
	
	@GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
    		@PathVariable Long categoryId,
    		@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "name") String sortBy){
		
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		Page<ProductResponse> response = productService.getProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("search/{name}")
	public ResponseEntity<Page<ProductResponse>> searchProductsByName(
	        @PathVariable String name,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "name") String sortBy){
	    
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
		Page<ProductResponse> response = productService.getProductsByName(name, pageable);
        return ResponseEntity.ok(response);
    }
	
	@PatchMapping("/{id}")
	public ResponseEntity<Void> updateProductProperties(
	        @PathVariable Long id,
	        @RequestBody ProductRequest request) {

	    productService.updateProductProperties(id, request);
	    return ResponseEntity.ok().build();
	}
}
