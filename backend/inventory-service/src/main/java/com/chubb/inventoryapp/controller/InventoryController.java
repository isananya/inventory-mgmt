package com.chubb.inventoryapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.InventoryRequest;
import com.chubb.inventoryapp.dto.InventoryResponse;
import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.dto.StockRequest;
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
	
	@PatchMapping("/{id}")
    public ResponseEntity<Void> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        inventoryService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("stock/add")
    public ResponseEntity<Void> addStock(@Valid @RequestBody List<StockRequest> request) {

        inventoryService.addStock(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("stock/deduct")
    public ResponseEntity<Void> deductStock(@Valid @RequestBody List<StockRequest> request) {

        inventoryService.deductStock(request);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> lowStock() {
        return ResponseEntity.ok(inventoryService.getLowStock());
    }
    
    @PostMapping("/check")
    public List<StockCheckResponse> checkStock(@RequestBody List<StockRequest> request) {
        return inventoryService.checkStock(request);
    }
}
