package com.chubb.inventoryapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.CategoryRequest;
import com.chubb.inventoryapp.dto.CategoryResponse;
import com.chubb.inventoryapp.exception.CategoryAlreadyExistsException;
import com.chubb.inventoryapp.model.Category;
import com.chubb.inventoryapp.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

	public CategoryService(CategoryRepository categoryRepository) {
		super();
		this.categoryRepository = categoryRepository;
	}

	public Long addCategory(CategoryRequest request) {

        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new CategoryAlreadyExistsException(request.getName());
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIconUrl(request.getIconUrl());

        categoryRepository.save(category);
        
        return category.getId();
    }
	
	public List<CategoryResponse> getAllCategories() {
		return categoryRepository.findAll()
	            .stream()
	            .map(category -> new CategoryResponse(
	                    category.getId(),
	                    category.getName(),
	                    category.getDescription(),
	                    category.getIconUrl()
	            ))
	            .toList();
    }
}
