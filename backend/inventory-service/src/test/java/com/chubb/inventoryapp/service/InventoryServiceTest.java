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
    void addInventory_ProductNotFound_ThrowsException() {
        InventoryRequest request = new InventoryRequest(99L, 2L, 100, 10);
        when(inventoryRepository.existsByProductIdAndWarehouseId(99L, 2L)).thenReturn(false);
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> inventoryService.addInventory(request));
    }

    @Test
    void addInventory_WarehouseNotFound_ThrowsException() {
        InventoryRequest request = new InventoryRequest(1L, 99L, 100, 10);
        when(inventoryRepository.existsByProductIdAndWarehouseId(1L, 99L)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> inventoryService.addInventory(request));
    }

    @Test
    void getInventoryByProduct_Success() {
        Product product = new Product();
        product.setId(1L);
        
        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setProduct(product);
        inventory.setWarehouse(new Warehouse());
        inventory.setQuantity(50);
        inventory.setLowStockThreshold(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProduct(product)).thenReturn(List.of(inventory));

        List<InventoryResponse> responses = inventoryService.getInventoryByProduct(1L);

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
        assertEquals(50, responses.get(0).getQuantity());
    }

    @Test
    void getInventoryByProduct_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> inventoryService.getInventoryByProduct(1L));
    }

    @Test
    void getInventoryByWarehouse_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(2L);

        Inventory inventory = new Inventory();
        inventory.setId(10L);
        inventory.setProduct(new Product());
        inventory.setWarehouse(warehouse);
        inventory.setQuantity(20);

        when(warehouseRepository.findById(2L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByWarehouse(warehouse)).thenReturn(List.of(inventory));

        List<InventoryResponse> responses = inventoryService.getInventoryByWarehouse(2L);

        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getId());
    }

    @Test
    void getInventoryByWarehouse_NotFound_ThrowsException() {
        when(warehouseRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(WarehouseNotFoundException.class, () -> inventoryService.getInventoryByWarehouse(2L));
    }

    @Test
    void updateQuantity_Success() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(10);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        inventoryService.updateQuantity(1L, 50);

        assertEquals(50, inventory.getQuantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void updateQuantity_NotFound_ThrowsException() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(InventoryNotFoundException.class, () -> inventoryService.updateQuantity(1L, 50));
    }

    @Test
    void addStock_Success() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setWarehouseId(2L);
        req.setQuantity(10);

        Inventory inventory = new Inventory();
        inventory.setQuantity(20);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.of(inventory));

        inventoryService.addStock(Collections.singletonList(req));

        assertEquals(30, inventory.getQuantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void addStock_InventoryNotFound_ThrowsException() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setWarehouseId(2L);
        req.setQuantity(10);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> inventoryService.addStock(Collections.singletonList(req)));
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
    void deductStock_InventoryNotFound_ThrowsException() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setWarehouseId(2L);
        req.setQuantity(5);

        when(inventoryRepository.findByProductIdAndWarehouseId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(InventoryNotFoundException.class, () -> inventoryService.deductStock(Collections.singletonList(req)));
    }

    @Test
    void getLowStock_ReturnsFilteredList() {
        Inventory inv1 = new Inventory();
        inv1.setQuantity(5);
        inv1.setLowStockThreshold(10); // Low stock
        inv1.setProduct(new Product());
        inv1.setWarehouse(new Warehouse());

        Inventory inv2 = new Inventory();
        inv2.setQuantity(20);
        inv2.setLowStockThreshold(10); // Not low stock

        when(inventoryRepository.findAll()).thenReturn(List.of(inv1, inv2));

        List<InventoryResponse> result = inventoryService.getLowStock();

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getQuantity());
    }

    @Test
    void checkStock_Available_ReturnsTrue() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setQuantity(5);

        Inventory inventory = new Inventory();
        Warehouse w = new Warehouse(); 
        w.setId(1L);
        Product p = new Product(); 
        p.setPrice(100f);
        inventory.setWarehouse(w);
        inventory.setProduct(p);

        when(inventoryRepository.findAvailableStock(1L, 5)).thenReturn(List.of(inventory));

        List<StockCheckResponse> responses = inventoryService.checkStock(List.of(req));

        assertTrue(responses.get(0).isAvailable());
        assertEquals(100f, responses.get(0).getPrice());
    }

    @Test
    void checkStock_Unavailable_ReturnsFalse() {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setQuantity(5);

        when(inventoryRepository.findAvailableStock(1L, 5)).thenReturn(Collections.emptyList());

        List<StockCheckResponse> responses = inventoryService.checkStock(List.of(req));

        assertFalse(responses.get(0).isAvailable());
        assertNull(responses.get(0).getWarehouseId());
    }
}