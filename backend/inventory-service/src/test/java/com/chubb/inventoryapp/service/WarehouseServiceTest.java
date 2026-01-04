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

import com.chubb.inventoryapp.dto.WarehouseRequest;
import com.chubb.inventoryapp.dto.WarehouseResponse;
import com.chubb.inventoryapp.exception.WarehouseAlreadyExistsException;
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
    void addWarehouse_Duplicate_ThrowsException() {
        WarehouseRequest request = new WarehouseRequest("Main WH", "New York");
        when(warehouseRepository.existsByNameIgnoreCaseAndLocationIgnoreCase("Main WH", "New York")).thenReturn(true);

        assertThrows(WarehouseAlreadyExistsException.class, () -> warehouseService.addWarehouse(request));
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    @Test
    void getAllWarehouses_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main WH");
        
        when(warehouseRepository.findAll()).thenReturn(Collections.singletonList(warehouse));

        List<WarehouseResponse> responses = warehouseService.getAllWarehouses();

        assertEquals(1, responses.size());
        assertEquals("Main WH", responses.get(0).getName());
    }

    @Test
    void getActiveWarehouses_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main WH");
        warehouse.setActive(true);

        when(warehouseRepository.findByActiveTrue()).thenReturn(Collections.singletonList(warehouse));

        List<WarehouseResponse> responses = warehouseService.getActiveWarehouses();

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
    }

    @Test
    void getWarehouseById_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Main WH");

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        WarehouseResponse response = warehouseService.getWarehouseById(1L);

        assertEquals(1L, response.getId());
        assertEquals("Main WH", response.getName());
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
        assertEquals("New Loc", warehouse.getLocation());
        verify(warehouseRepository).save(warehouse);
    }

    @Test
    void updateWarehouse_NotFound_ThrowsException() {
        WarehouseRequest request = new WarehouseRequest("New Name", "New Loc");
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.updateWarehouse(request, 99L));
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
    void deactivateWarehouse_NotFound_ThrowsException() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.deactivateWarehouse(99L));
    }

    @Test
    void activateWarehouse_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setActive(false);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        warehouseService.activateWarehouse(1L);

        assertTrue(warehouse.isActive());
        verify(warehouseRepository).save(warehouse);
    }
    
    @Test
    void activateWarehouse_NotFound_ThrowsException() {
        when(warehouseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(WarehouseNotFoundException.class, () -> warehouseService.activateWarehouse(99L));
    }
}