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
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.WarehouseRequest;
import com.chubb.inventoryapp.dto.WarehouseResponse;
import com.chubb.inventoryapp.service.WarehouseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

	public WarehouseController(WarehouseService warehouseService) {
		super();
		this.warehouseService = warehouseService;
	}

	@PostMapping
	public ResponseEntity<Long> addWarehouse(@Valid @RequestBody WarehouseRequest request) {
		Long warehouseId = warehouseService.addWarehouse(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(warehouseId);
	}
	
	@GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }
	
	@GetMapping("/active")
    public ResponseEntity<List<WarehouseResponse>> getActiveWarehouses() {
        return ResponseEntity.ok(warehouseService.getActiveWarehouses());
    }
	
	@GetMapping("/{id}")
	public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id){
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<Void> updateWarehouse(
            @PathVariable Long id,
            @RequestBody WarehouseRequest request) {
		warehouseService.updateWarehouse(request, id);
        return ResponseEntity.ok(null);
    }
	
	@PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateWarehouse(@PathVariable Long id) {
        warehouseService.deactivateWarehouse(id);
        return ResponseEntity.ok(null);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateWarehouse(@PathVariable Long id) {
        warehouseService.activateWarehouse(id);
        return ResponseEntity.ok(null);
    }

}
