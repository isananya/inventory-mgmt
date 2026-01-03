package com.chubb.inventoryapp.feign;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.exception.OrderServiceUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderClientWrapper {
	
	private final OrderClient orderClient;
	
	@CircuitBreaker(name = "orderServiceCB", fallbackMethod = "orderFallback")
    public OrderResponse getOrderById(Long id) {
		return orderClient.getOrderById(id);
	}

	public OrderResponse orderFallback(Long id, Throwable ex) {
		throw new OrderServiceUnavailableException("Order Service is currently unavailable.");
	}

}
