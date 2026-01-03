package com.chubb.inventoryapp.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.OrderItemResponse;
import com.chubb.inventoryapp.dto.OrderRequest;
import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.dto.OrderStatusUpdateRequest;
import com.chubb.inventoryapp.model.OrderStatus;
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
	public ResponseEntity<Page<OrderResponse>> getOrdersByCustomer(
	        @PathVariable Long customerId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "id") String sortBy) {
	    
	    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
	    Page<OrderResponse> response = orderService.getOrdersByCustomer(customerId, pageable);
	    
	    return ResponseEntity.ok(response);
	}
	
	@PutMapping("/{id}/cancel")
	public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
	    orderService.cancelOrder(id);
	    return ResponseEntity.ok(null);
	}
	
	@GetMapping
	public ResponseEntity<Page<OrderResponse>> getAllOrders(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "id") String sortBy) {
	    
	    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
	    Page<OrderResponse> response = orderService.getAllOrders(pageable);
	    
	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{id}/status")
	public ResponseEntity<OrderStatusUpdateRequest> getOrderStatus(@PathVariable Long id) {
	    OrderStatus status = orderService.getOrderStatus(id);
	    return ResponseEntity.ok(new OrderStatusUpdateRequest(status));
	}
	
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> updateOrderStatus(
	        @PathVariable Long id, 
	        @RequestBody @Valid OrderStatusUpdateRequest request) {
	    
	    orderService.updateOrderStatus(id, request.getStatus());
	    return ResponseEntity.ok(null);
	}
	
	@GetMapping("/{id}/items")
	public ResponseEntity<List<OrderItemResponse>> getOrderItems(@PathVariable Long id) {
	    return ResponseEntity.ok(orderService.getOrderItems(id));
	}
}
