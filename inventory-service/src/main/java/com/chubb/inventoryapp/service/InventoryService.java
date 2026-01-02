package com.chubb.inventoryapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.InventoryRequest;
import com.chubb.inventoryapp.dto.InventoryResponse;
import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.dto.StockRequest;
import com.chubb.inventoryapp.exception.InsufficientStockException;
import com.chubb.inventoryapp.exception.InventoryAlreadyExistsException;
import com.chubb.inventoryapp.exception.InventoryNotFoundException;
import com.chubb.inventoryapp.exception.ProductNotFoundException;
import com.chubb.inventoryapp.exception.WarehouseNotFoundException;
import com.chubb.inventoryapp.model.Inventory;
import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Warehouse;
import com.chubb.inventoryapp.repository.InventoryRepository;
import com.chubb.inventoryapp.repository.ProductRepository;
import com.chubb.inventoryapp.repository.WarehouseRepository;

import jakarta.transaction.Transactional;

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
	
	private InventoryResponse mapToResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct(),
                inventory.getWarehouse(),
                inventory.getQuantity(),
                inventory.getLowStockThreshold()
        );
	}
    
	public List<InventoryResponse> getInventoryByProduct(Long id){
		Product product = productRepository.findById(id).orElseThrow(()-> new ProductNotFoundException());
		
		return inventoryRepository.findByProduct(product)
                .stream()
                .map(this::mapToResponse)
                .toList();
	}
	
	public List<InventoryResponse> getInventoryByWarehouse(Long id){
		Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(()-> new WarehouseNotFoundException(id));
		
		return inventoryRepository.findByWarehouse(warehouse)
                .stream()
                .map(this::mapToResponse)
                .toList();
	}

	public void updateQuantity(Long id, Integer quantity ) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found"));

        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
    }

	@Transactional
	public void addStock(List<StockRequest> requests) {
	    for (StockRequest req : requests) {
	        Inventory inventory = inventoryRepository
	                .findByProductIdAndWarehouseId(req.getProductId(), req.getWarehouseId())
	                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for Product " + req.getProductId()));

	        inventory.setQuantity(inventory.getQuantity() + req.getQuantity());
	        inventoryRepository.save(inventory);
	    }
	}

	@Transactional
	public void deductStock(List<StockRequest> requests) {
	    for (StockRequest req : requests) {
	        Inventory inventory = inventoryRepository
	                .findByProductIdAndWarehouseId(req.getProductId(), req.getWarehouseId())
	                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for Product " + req.getProductId()));

	        if (inventory.getQuantity() < req.getQuantity()) {
	            throw new InsufficientStockException("Not enough stock for Product " + req.getProductId());
	        }

	        inventory.setQuantity(inventory.getQuantity() - req.getQuantity());
	        inventoryRepository.save(inventory);
	    }
	}
    
    public List<InventoryResponse> getLowStock() {
        return inventoryRepository.findAll()
                .stream()
                .filter(i -> i.getQuantity() <= i.getLowStockThreshold())
                .map(this::mapToResponse)
                .toList();
    }
    
    public List<StockCheckResponse> checkStock(List<StockRequest> requests) {
        List<StockCheckResponse> responses = new ArrayList<>();

        for (StockRequest req : requests) {
            Optional<Inventory> inventoryOpt = inventoryRepository
                    .findFirstByProductIdAndQuantityGreaterThanEqual(req.getProductId(), req.getQuantity());

            if (inventoryOpt.isPresent()) {
                Inventory inventory = inventoryOpt.get();
                responses.add(new StockCheckResponse(req.getProductId(), true, 
                    inventory.getWarehouse().getId(), inventory.getProduct().getPrice()));
            } 
            else {
                responses.add(new StockCheckResponse(req.getProductId(), false, null,0));
            }
        }

        return responses;
    }
}
