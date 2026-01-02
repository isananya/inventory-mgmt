package com.chubb.inventoryapp.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.InventoryCheckResponse;
import com.chubb.inventoryapp.dto.OrderItemRequest;
import com.chubb.inventoryapp.dto.StockUpdateRequest;
import com.chubb.inventoryapp.exception.InventoryServiceUnavailableException;

@Service
@RequiredArgsConstructor
public class InventoryClientWrapper {

    private final InventoryClient inventoryClient;

    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "checkStockFallback")
    public InventoryCheckResponse checkStock(OrderItemRequest request) {
        return inventoryClient.checkStock(request);
    }

    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "voidFallback")
    public void deductStock(StockUpdateRequest request) {
        inventoryClient.deductStock(request);
    }

    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "voidFallback")
    public void addStock(StockUpdateRequest request) {
        inventoryClient.addStock(request);
    }

    public InventoryCheckResponse checkStockFallback(OrderItemRequest request, Throwable ex) {
        throw new InventoryServiceUnavailableException("Inventory Service is currently unavailable.");
    }

    public void voidFallback(StockUpdateRequest request, Throwable ex) {
        throw new InventoryServiceUnavailableException("Inventory Service is currently unavailable.");
    }
}