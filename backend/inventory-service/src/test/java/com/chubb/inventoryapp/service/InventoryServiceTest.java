package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.inventoryapp.dto.InventoryRequest;
import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.dto.StockRequest;
import com.chubb.inventoryapp.exception.InsufficientStockException;
import com.chubb.inventoryapp.exception.InventoryAlreadyExistsException;
import com.chubb.inventoryapp.model.Inventory;
import com.chubb.inventoryapp.model.Product;
import com.chubb.inventoryapp.model.Warehouse;
import com.chubb.inventoryapp.repository.InventoryRepository;
import com.chubb.inventoryapp.repository.ProductRepository;
import com.chubb.inventoryapp.repository.WarehouseRepository;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private ProductRepository productRepository;
    @Mock private WarehouseRepository warehouseRepository;

    @InjectMocks private InventoryService inventoryService;

    @Test
    void addInventory_Success() {
        InventoryRequest request = new InventoryRequest(1L, 2L, 100, 10);
        
        when(inventoryRepository.existsByProductIdAndWarehouseId(1L, 2L)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(new Warehouse()));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> {
            Inventory inv = i.getArgument(0);
            inv.setId(5L);
            return inv;
        });

        Long id = inventoryService.addInventory(request);
        assertEquals(5L, id);
    }

    @Test
    void addInventory_Duplicate_ThrowsException() {
        InventoryRequest request = new InventoryRequest(1L, 2L, 100, 10);
        when(inventoryRepository.existsByProductIdAndWarehouseId(1L, 2L)).thenReturn(true);
        assertThrows(InventoryAlreadyExistsException.class, () -> inventoryService.addInventory(request));
    }

    @Test
    void deductStock_Success() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setWarehouseId(2L);
        req.setQuantity(5);

        Inventory inventory = new Inventory();
        inventory.setQuantity(10);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.of(inventory));

        inventoryService.deductStock(Collections.singletonList(req));

        assertEquals(5, inventory.getQuantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void deductStock_Insufficient_ThrowsException() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setWarehouseId(2L);
        req.setQuantity(20);

        Inventory inventory = new Inventory();
        inventory.setQuantity(10);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.of(inventory));

        assertThrows(InsufficientStockException.class, () -> inventoryService.deductStock(Collections.singletonList(req)));
    }

    @Test
    void checkStock_ReturnsStatus() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setQuantity(5);

        Inventory inventory = new Inventory();
        Warehouse w = new Warehouse(); w.setId(1L);
        Product p = new Product(); p.setPrice(100f);
        inventory.setWarehouse(w);
        inventory.setProduct(p);

        when(inventoryRepository.findAvailableStock(1L, 5)).thenReturn(Optional.of(inventory));

        List<StockCheckResponse> responses = inventoryService.checkStock(Collections.singletonList(req));

        assertTrue(responses.get(0).isAvailable());
        assertEquals(100f, responses.get(0).getPrice());
    }
}