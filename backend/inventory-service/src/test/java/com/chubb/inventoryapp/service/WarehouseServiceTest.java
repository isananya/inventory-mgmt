package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chubb.inventoryapp.dto.WarehouseRequest;
import com.chubb.inventoryapp.exception.WarehouseNotFoundException;
import com.chubb.inventoryapp.model.Warehouse;
import com.chubb.inventoryapp.repository.WarehouseRepository;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseService warehouseService;

    @Test
    void addWarehouse_Success() {
        WarehouseRequest request = new WarehouseRequest("Main WH", "New York");
        when(warehouseRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("Main WH", "New York")).thenReturn(false);
        when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(i -> {
            Warehouse w = i.getArgument(0);
            w.setId(1L);
            return w;
        });

        Long id = warehouseService.addWarehouse(request);
        assertEquals(1L, id);
    }

    @Test
    void deactivateWarehouse_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setActive(true);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.deactivateWarehouse(1L);

        assertFalse(warehouse.isActive());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void getWarehouseById_NotFound_ThrowsException() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.getWarehouseById(99L));
    }
    
    @Test
    void updateWarehouse_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Old Name");
        
        WarehouseRequest request = new WarehouseRequest("New Name", "New Loc");
        
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        
        warehouseService.updateWarehouse(request, 1L);
        
        assertEquals("New Name", warehouse.getName());
        verify(warehouseRepository).save(warehouse);
    }
}