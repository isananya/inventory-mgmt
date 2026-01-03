package com.chubb.inventoryapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.CategoryRequest;
import com.chubb.inventoryapp.dto.CategoryResponse;
import com.chubb.inventoryapp.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryController {
	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		super();
		this.categoryService = categoryService;
	}
	
	@PostMapping
    public ResponseEntity<Long> addCategory(@Valid @RequestBody CategoryRequest request) {
        Long categoryId = categoryService.addCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryId);
    }
	
	@GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }	
}
