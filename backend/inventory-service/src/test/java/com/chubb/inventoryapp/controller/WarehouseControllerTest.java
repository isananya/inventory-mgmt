package com.chubb.inventoryapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.chubb.inventoryapp.dto.WarehouseRequest;
import com.chubb.inventoryapp.dto.WarehouseResponse;
import com.chubb.inventoryapp.service.WarehouseService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class WarehouseControllerTest {

    private MockMvc mockMvc;
    @Mock private WarehouseService warehouseService;
    @InjectMocks private WarehouseController warehouseController;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(warehouseController).build();
    }

    @Test
    void addWarehouse_Success() throws Exception {
        WarehouseRequest request = new WarehouseRequest("North WH", "Loc A");
        when(warehouseService.addWarehouse(any(WarehouseRequest.class))).thenReturn(10L);

        mockMvc.perform(post("/warehouse")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("10"));
    }

    @Test
    void getWarehouseById_Success() throws Exception {
        WarehouseResponse resp = new WarehouseResponse(1L, "North WH", "Loc A", true);
        when(warehouseService.getWarehouseById(1L)).thenReturn(resp);

        mockMvc.perform(get("/warehouse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("North WH"));
    }

    @Test
    void deactivateWarehouse_Success() throws Exception {
        doNothing().when(warehouseService).deactivateWarehouse(1L);

        mockMvc.perform(patch("/warehouse/1/deactivate"))
                .andExpect(status().isOk());
    }
}