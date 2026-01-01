package com.chubb.inventoryapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chubb.inventoryapp.model.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByNameIgnoreCaseAndLocationIgnoreCase(String name, String location);
    List<Warehouse> findByActiveTrue();
}