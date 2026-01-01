package com.chubb.inventoryapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.InventoryRequest;
import com.chubb.inventoryapp.dto.InventoryResponse;
import com.chubb.inventoryapp.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

	public InventoryController(InventoryService inventoryService) {
		super();
		this.inventoryService = inventoryService;
	}
    
	@PostMapping
	public ResponseEntity<Long> addInventory(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addInventory(request));
    }
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<List<InventoryResponse>> getInventoryByProduct(@PathVariable Long productId){
		List<InventoryResponse> response = inventoryService.getInventoryByProduct(productId);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/warehouse/{warehouseId}")
	public ResponseEntity<List<InventoryResponse>> getInventoryByWarehouse(@PathVariable Long warehouseId){
		List<InventoryResponse> response = inventoryService.getInventoryByWarehouse(warehouseId);
		return ResponseEntity.ok(response);
	}

}
