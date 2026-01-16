package com.chubb.inventoryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chubb.inventoryapp.model.Inventory;
import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Warehouse;


public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductIdAndWarehouseId(Long productId, Long warehouseId);

    boolean existsByProductIdAndWarehouseId(Long productId, Long warehouseId);

	List<Inventory> findByProduct(Product product);
	
	List<Inventory> findByWarehouse(Warehouse warehouse);

	@Query("SELECT i FROM Inventory i " +
           "WHERE i.product.id = :productId " +
           "AND i.quantity >= :quantity " +
           "AND i.warehouse.active = true") 
    List<Inventory> findAvailableStock(@Param("productId") Long productId, 
                                           @Param("quantity") Integer quantity);
}
	