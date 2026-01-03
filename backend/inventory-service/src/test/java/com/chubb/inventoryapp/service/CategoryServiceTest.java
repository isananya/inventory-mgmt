package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.inventoryapp.dto.CategoryRequest;
import com.chubb.inventoryapp.dto.CategoryResponse;
import com.chubb.inventoryapp.exception.CategoryAlreadyExistsException;
import com.chubb.inventoryapp.model.Category;
import com.chubb.inventoryapp.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void addCategory_Success() {
        CategoryRequest request = new CategoryRequest("Electronics", "Gadgets", "icon.png");
        
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Long id = categoryService.addCategory(request);

        assertEquals(1L, id);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void addCategory_Duplicate_ThrowsException() {
        CategoryRequest request = new CategoryRequest("Electronics", "Desc", null);
        when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.addCategory(request));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void getAllCategories_Success() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        
        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertEquals(1, responses.size());
        assertEquals("Electronics", responses.get(0).getName());
    }
}