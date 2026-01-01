package com.chubb.inventoryapp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.WarehouseRequest;
import com.chubb.inventoryapp.dto.WarehouseResponse;
import com.chubb.inventoryapp.exception.WarehouseAlreadyExistsException;
import com.chubb.inventoryapp.model.Warehouse;
import com.chubb.inventoryapp.repository.WarehouseRepository;

@Service
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

	public WarehouseService(WarehouseRepository warehouseRepository) {
		super();
		this.warehouseRepository = warehouseRepository;
	}
    
	public Long addWarehouse(WarehouseRequest request) {
		if (warehouseRepository.existsByNameIgnoreCaseAndLocationIgnoreCase(request.getName(),request.getLocation())) {
            throw new WarehouseAlreadyExistsException(request.getName(), request.getLocation());
        }

        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        warehouse.setActive(true);

        warehouseRepository.save(warehouse);
        return warehouse.getId();
	}
	
	 private WarehouseResponse mapToResponse(Warehouse warehouse) {
	        return new WarehouseResponse(
	                warehouse.getId(),
	                warehouse.getName(),
	                warehouse.getLocation(),
	                warehouse.isActive()
	        );
	    }
    
	public List<WarehouseResponse> getAllWarehouses(){
		return warehouseRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
	}
	
	public List<WarehouseResponse> getActiveWarehouses(){
		return warehouseRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
	}

}
