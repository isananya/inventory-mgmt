package com.chubb.inventoryapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.OrderRequest;
import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/order")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		super();
		this.orderService = orderService;
	}

	@PostMapping
	public ResponseEntity<Long> placeOrder(@Valid @RequestBody OrderRequest request) {
		Long id = orderService.placeOrder(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(id);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
	    return ResponseEntity.ok(orderService.getOrderById(id));
	}
	
	@GetMapping("/customer/{customerId}")
	public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@PathVariable Long customerId) {
	    return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
	}

}
