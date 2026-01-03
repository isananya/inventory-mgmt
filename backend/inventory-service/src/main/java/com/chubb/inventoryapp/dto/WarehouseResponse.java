package com.chubb.inventoryapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
	private Long id;
	private String name;
	private String location;
	private boolean active;
}
