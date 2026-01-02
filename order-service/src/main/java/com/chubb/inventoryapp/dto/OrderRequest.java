package com.chubb.inventoryapp.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
	@NotNull
    private Long customerId;

    @NotBlank
    private String deliveryAddress;
    
    @NotEmpty
    private List<OrderItemRequest> items;
}

