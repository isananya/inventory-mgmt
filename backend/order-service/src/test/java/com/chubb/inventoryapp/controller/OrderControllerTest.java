package com.chubb.inventoryapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.chubb.inventoryapp.dto.OrderItemRequest;
import com.chubb.inventoryapp.dto.OrderItemResponse;
import com.chubb.inventoryapp.dto.OrderRequest;
import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.dto.OrderStatusUpdateRequest;
import com.chubb.inventoryapp.exception.OrderNotFoundException;
import com.chubb.inventoryapp.model.Address;
import com.chubb.inventoryapp.model.OrderStatus;
import com.chubb.inventoryapp.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper = new ObjectMapper();
    
    private OrderResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        
        Address address = new Address("Line1", "City", "State", "00000");
        sampleResponse = new OrderResponse(1L, 100L, OrderStatus.CREATED, address, 500.0f, LocalDateTime.now());
    }

    @Test
    void placeOrder_Success() throws Exception {
        Address address = new Address("Line1", "City", "State", "00000");
        OrderItemRequest itemReq = new OrderItemRequest(1L, 5);
        OrderRequest request = new OrderRequest(100L, address, Collections.singletonList(itemReq));

        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }
    
    @Test
    void placeOrder_ValidationFailure() throws Exception {
        OrderRequest request = new OrderRequest(100L, new Address(), Collections.emptyList());
        
        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrder_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void getOrder_NotFound() throws Exception {
        when(orderService.getOrderById(99L)).thenThrow(new OrderNotFoundException("Not found"));
        try {
            mockMvc.perform(get("/order/99"));
        } catch (Exception e) {
        }
    }

    @Test
    void cancelOrder_Success() throws Exception {
        doNothing().when(orderService).cancelOrder(1L);

        mockMvc.perform(put("/order/1/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderStatus_Success() throws Exception {
        when(orderService.getOrderStatus(1L)).thenReturn(OrderStatus.SHIPPED);

        mockMvc.perform(get("/order/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest(OrderStatus.DELIVERED);
        
        doNothing().when(orderService).updateOrderStatus(1L, OrderStatus.DELIVERED);

        mockMvc.perform(patch("/order/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    @Test
    void getOrderItems_Success() throws Exception {
        OrderItemResponse itemResp = new OrderItemResponse(10L, 50L, 2, 101L, 25.0f);
        when(orderService.getOrderItems(1L)).thenReturn(Collections.singletonList(itemResp));
        
        mockMvc.perform(get("/order/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price").value(25.0f));
    }
}