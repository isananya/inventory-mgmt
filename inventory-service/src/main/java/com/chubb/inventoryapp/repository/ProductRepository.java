package com.chubb.inventoryapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chubb.inventoryapp.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByBrandAndName(String brand, String name);

    List<Product> findByCategoryId(Long categoryId);
}
