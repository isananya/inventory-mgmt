package com.chubb.inventoryapp.model;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Product code is required")
	@Size(min = 3, max = 7)
	@Column(nullable = false)
	private String productCode;

	@NotBlank(message = "Product name is required")
	@Size(min = 2, max = 100)
	@Column(nullable = false)
	private String name;
	
	@NotBlank(message = "Product brand is required")
	@Size(min = 2, max = 100)
	@Column(nullable = false)
	private String brand;

	@NotNull(message = "Price is required")
	@Positive(message = "Price must be greater than 0")
	@Column(nullable = false)
	private Float price;

	@Column(length = 500)
	private String description;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "json")
	private Map<String, Object> specifications = new HashMap<>();

	private String imageUrl;
	
	@NotNull(message = "Category is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Category category;
}
