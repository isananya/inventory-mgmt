package com.chubb.inventoryapp.dto;

import java.util.List;

import com.chubb.inventoryapp.model.Address;

import jakarta.validation.Valid;
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

    @NotNull
    @Valid
    private Address address;
    
    @NotEmpty
    private List<OrderItemRequest> items;
}

