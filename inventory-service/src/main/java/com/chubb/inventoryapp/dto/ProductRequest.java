package com.chubb.inventoryapp.dto;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
	@NotBlank(message = "Product name is required")
	@Size(min = 2, max = 100)
	private String name;
	
	@NotBlank(message = "Product brand is required")
	@Size(min = 2, max = 100)
	private String brand;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	private Float price;

	private String description;

	private Map<String, Object> specifications = new HashMap<>();

	private String imageUrl;

	private Long categoryId;
}
