package com.chubb.inventoryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chubb.inventoryapp.model.Inventory;
import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Warehouse;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);

	List<Inventory> findByProduct(Product product);
	
	List<Inventory> findByWarehouse(Warehouse warehouse);

	Optional<Inventory> findFirstByProductIdAndQuantityGreaterThanEqual(Long productId, Integer quantity);
}
	