package com.chubb.inventoryapp.service;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.InventoryRequest;
import com.chubb.inventoryapp.exception.InventoryAlreadyExistsException;
import com.chubb.inventoryapp.exception.ProductNotFoundException;
import com.chubb.inventoryapp.exception.WarehouseNotFoundException;
import com.chubb.inventoryapp.model.Inventory;
import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Warehouse;
import com.chubb.inventoryapp.repository.InventoryRepository;
import com.chubb.inventoryapp.repository.ProductRepository;
import com.chubb.inventoryapp.repository.WarehouseRepository;

@Service
public class InventoryService {
	private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    
	public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository,
			WarehouseRepository warehouseRepository) {
		super();
		this.inventoryRepository = inventoryRepository;
		this.productRepository = productRepository;
		this.warehouseRepository = warehouseRepository;
	}
	
	public Long addInventory(InventoryRequest request) {

        if (inventoryRepository.existsByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId())) {
            throw new InventoryAlreadyExistsException(request.getProductId(), request.getWarehouseId());
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException());

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new WarehouseNotFoundException(request.getWarehouseId()));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(request.getQuantity());
        inventory.setLowStockThreshold(request.getLowStockThreshold());

        inventoryRepository.save(inventory);
        return inventory.getId();
    }
    

}
