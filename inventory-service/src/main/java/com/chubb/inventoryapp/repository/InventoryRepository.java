package com.chubb.inventoryapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chubb.inventoryapp.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);
}
	