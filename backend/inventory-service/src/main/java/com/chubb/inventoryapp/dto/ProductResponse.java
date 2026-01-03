package com.chubb.inventoryapp.dto;

import java.util.HashMap;
import java.util.Map;

import com.chubb.inventoryapp.model.Category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
	private Long id;
	private String productCode;
	private String name;
	private String brand;
	private Float price;
	private String description;
	private Map<String, Object> specifications = new HashMap<>();
	private String imageUrl;
	private Category category;
}
