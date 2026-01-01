package com.chubb.inventoryapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Category;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductCode(String productCode);
    
    List<Product> findByCategory(Category category);
    
    List<Product> findByNameContainingIgnoreCase(String name);
}
