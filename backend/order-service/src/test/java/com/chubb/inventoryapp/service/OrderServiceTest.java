package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.chubb.inventoryapp.dto.OrderItemRequest;
import com.chubb.inventoryapp.dto.OrderItemResponse;
import com.chubb.inventoryapp.dto.OrderRequest;
import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.exception.OrderNotFoundException;
import com.chubb.inventoryapp.exception.OutOfStockException;
import com.chubb.inventoryapp.exception.StatusConflictException;
import com.chubb.inventoryapp.feign.InventoryClientWrapper;
import com.chubb.inventoryapp.model.Address;
import com.chubb.inventoryapp.model.Order;
import com.chubb.inventoryapp.model.OrderItem;
import com.chubb.inventoryapp.model.OrderStatus;
import com.chubb.inventoryapp.repository.OrderItemRepository;
import com.chubb.inventoryapp.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private InventoryClientWrapper inventoryClient;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;
    private Order order;
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address("123 St", "City", "State", "12345");

        OrderItemRequest itemReq = new OrderItemRequest(1L, 2);
        orderRequest = new OrderRequest(100L, address, Collections.singletonList(itemReq));

        order = new Order();
        order.setId(1L);
        order.setCustomerId(100L);
        order.setStatus(OrderStatus.CREATED);
        order.setAddress(address);
        order.setItems(new ArrayList<>());
        
        OrderItem item = new OrderItem(1L, 1L, 101L, 2, 50.0f, order);
        order.getItems().add(item);
        order.setTotalAmount(100.0f);
    }

    @Test
    void placeOrder_Success() {
        StockCheckResponse stockRes = new StockCheckResponse();
        stockRes.setProductId(1L);
        stockRes.setAvailable(true);
        stockRes.setWarehouseId(101L);
        stockRes.setPrice(50.0f);

        when(inventoryClient.checkStock(anyList())).thenReturn(Collections.singletonList(stockRes));
        doNothing().when(inventoryClient).deductStock(anyList());
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        Long orderId = orderService.placeOrder(orderRequest);

        assertNotNull(orderId);
        assertEquals(1L, orderId);
        
        verify(inventoryClient).checkStock(anyList());
        verify(inventoryClient).deductStock(anyList());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_OutOfStock_ThrowsException() {
        StockCheckResponse stockRes = new StockCheckResponse();
        stockRes.setProductId(1L);
        stockRes.setAvailable(false);

        when(inventoryClient.checkStock(anyList())).thenReturn(Collections.singletonList(stockRes));

        assertThrows(OutOfStockException.class, () -> orderService.placeOrder(orderRequest));
        
        verify(inventoryClient, never()).deductStock(anyList());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertEquals(1L, response.getOrderId());
        assertEquals(100.0f, response.getTotalAmount());
        assertEquals(OrderStatus.CREATED, response.getStatus());
    }

    @Test
    void getOrderById_NotFound_ThrowsException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(99L));
    }
    
    @Test
    void getOrdersByCustomer_Success() {
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findByCustomerId(eq(100L), any(Pageable.class))).thenReturn(page);
        
        Page<OrderResponse> result = orderService.getOrdersByCustomer(100L, Pageable.unpaged());
        
        assertEquals(1, result.getContent().size());
        assertEquals(100L, result.getContent().get(0).getCustomerId());
    }

    @Test
    void cancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(inventoryClient).addStock(anyList());

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(inventoryClient).addStock(anyList());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_AlreadyCancelled_ThrowsException() {
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(StatusConflictException.class, () -> orderService.cancelOrder(1L));
        verify(inventoryClient, never()).addStock(anyList());
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_ToCancelled_TriggersCancelLogic() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        
        doNothing().when(inventoryClient).addStock(anyList());

        orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(inventoryClient).addStock(anyList());
    }

    @Test
    void updateOrderStatus_Conflict_ThrowsException() {
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(StatusConflictException.class, () -> orderService.updateOrderStatus(1L, OrderStatus.APPROVED));
    }
        
    @Test
    void getOrderItems_Success() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(order.getItems());
        
        List<OrderItemResponse> items = orderService.getOrderItems(1L);
        
        assertEquals(1, items.size());
        assertEquals(1L, items.get(0).getProductId());
    }
    
    @Test
    void getOrderItems_OrderNotFound_ThrowsException() {
        when(orderRepository.existsById(99L)).thenReturn(false);
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderItems(99L));
    }
}