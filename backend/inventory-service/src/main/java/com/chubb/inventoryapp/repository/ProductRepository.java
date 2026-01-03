package com.chubb.inventoryapp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Category;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByProductCode(String productCode);
    
    Page<Product> findByCategory(Category category, Pageable pageable);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
