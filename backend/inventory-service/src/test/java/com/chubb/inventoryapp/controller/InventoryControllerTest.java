package com.chubb.inventoryapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.chubb.inventoryapp.dto.InventoryRequest;
import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.dto.StockRequest;
import com.chubb.inventoryapp.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    private MockMvc mockMvc;
    @Mock private InventoryService inventoryService;
    @InjectMocks private InventoryController inventoryController;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
    }

    @Test
    void addInventory_Success() throws Exception {
        InventoryRequest req = new InventoryRequest(1L, 2L, 50, 5);
        when(inventoryService.addInventory(any(InventoryRequest.class))).thenReturn(55L);

        mockMvc.perform(post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().string("55"));
    }

    @Test
    void addStock_Success() throws Exception {
        StockRequest req = new StockRequest();
        req.setProductId(1L);
        req.setWarehouseId(2L);
        req.setQuantity(10);
        List<StockRequest> list = Collections.singletonList(req);
        
        doNothing().when(inventoryService).addStock(anyList());

        mockMvc.perform(put("/inventory/stock/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isOk());
    }

    @Test
    void checkStock_Success() throws Exception {
        StockRequest req = new StockRequest();
        List<StockRequest> list = Collections.singletonList(req);
        
        StockCheckResponse resp = new StockCheckResponse(1L, true, 2L, 10.0f);
        when(inventoryService.checkStock(anyList())).thenReturn(Collections.singletonList(resp));

        mockMvc.perform(post("/inventory/check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));
    }
}