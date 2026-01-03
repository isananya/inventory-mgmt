package com.chubb.inventoryapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.chubb.inventoryapp.dto.CategoryRequest;
import com.chubb.inventoryapp.dto.CategoryResponse;
import com.chubb.inventoryapp.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;
    @Mock private CategoryService categoryService;
    @InjectMocks private CategoryController categoryController;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void addCategory_Success() throws Exception {
        CategoryRequest request = new CategoryRequest("Books", "Description", "url");
        when(categoryService.addCategory(any(CategoryRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    void getAllCategories_Success() throws Exception {
        CategoryResponse resp = new CategoryResponse(1L, "Books", "Desc", "url");
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(resp));

        mockMvc.perform(get("/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Books"));
    }
}