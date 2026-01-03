package com.chubb.inventoryapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {
	@NotBlank(message = "Warehouse name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Location is required")
    @Size(max = 150)
    private String location;
}
