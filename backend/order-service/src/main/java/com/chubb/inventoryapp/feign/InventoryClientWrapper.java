package com.chubb.inventoryapp.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.dto.OrderItemRequest;
import com.chubb.inventoryapp.dto.StockUpdateRequest;
import com.chubb.inventoryapp.exception.InventoryServiceUnavailableException;

@Service
@RequiredArgsConstructor
public class InventoryClientWrapper {

    private final InventoryClient inventoryClient;

    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "checkStockFallback")
    public List<StockCheckResponse> checkStock(List<OrderItemRequest> request) {
        return inventoryClient.checkStock(request);
    }

    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "voidFallback")
    public void deductStock(List<StockUpdateRequest> request) {
        inventoryClient.deductStock(request);
    }

    @CircuitBreaker(name = "inventoryServiceCB", fallbackMethod = "voidFallback")
    public void addStock(List<StockUpdateRequest> request) {
        inventoryClient.addStock(request);
    }

    public List<StockCheckResponse> checkStockFallback(List<OrderItemRequest> request, Throwable ex) {
        throw new InventoryServiceUnavailableException("Inventory Service is currently unavailable.");
    }

    public void voidFallback(List<StockUpdateRequest> request, Throwable ex) {
        throw new InventoryServiceUnavailableException("Inventory Service is currently unavailable.");
    }
}