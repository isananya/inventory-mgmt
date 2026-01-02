package com.chubb.inventoryapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.OrderRequest;
import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.dto.OrderItemRequest;
import com.chubb.inventoryapp.dto.OrderItemResponse;
import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.dto.StockUpdateRequest;
import com.chubb.inventoryapp.exception.OrderNotFoundException;
import com.chubb.inventoryapp.exception.OutOfStockException;
import com.chubb.inventoryapp.exception.StatusConflictException;
import com.chubb.inventoryapp.feign.InventoryClientWrapper;
import com.chubb.inventoryapp.model.FulfillmentStatus;
import com.chubb.inventoryapp.model.Order;
import com.chubb.inventoryapp.model.OrderItem;
import com.chubb.inventoryapp.model.OrderStatus;
import com.chubb.inventoryapp.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
    private final InventoryClientWrapper inventoryClient;
    
	public OrderService(OrderRepository orderRepository, InventoryClientWrapper inventoryClient) {
		super();
		this.orderRepository = orderRepository;
		this.inventoryClient = inventoryClient;
	}
    
	public Long placeOrder(OrderRequest request) {

        List<OrderItemRequest> stockRequests = request.getItems().stream()
                .map(i -> new OrderItemRequest(i.getProductId(), i.getQuantity()))
                .toList();

        List<StockCheckResponse> stockResponses = inventoryClient.checkStock(stockRequests);

        for (StockCheckResponse res : stockResponses) {
            if (!res.isAvailable()) {
                throw new OutOfStockException("Product out of stock: " + res.getProductId());
            }
        }

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());
        order.setStatus(OrderStatus.CREATED);
        order.setAddress(request.getAddress());
        order.setCreatedAt(LocalDateTime.now());

        float totalPrice = 0;
        for (int i = 0; i < request.getItems().size(); i++) {
            OrderItemRequest itemReq = request.getItems().get(i);
            StockCheckResponse stockRes = stockResponses.get(i);
            float price = itemReq.getQuantity()*stockRes.getPrice();
            totalPrice+= price;
           
            OrderItem item = new OrderItem();
            item.setProductId(itemReq.getProductId());
            item.setQuantity(itemReq.getQuantity());
            item.setWarehouseId(stockRes.getWarehouseId());
            item.setFulfillmentStatus(FulfillmentStatus.ASSIGNED);
            item.setPrice(price);
            item.setOrder(order);

            order.getItems().add(item);
        }
        
        order.setTotalAmount(totalPrice);

        List<StockUpdateRequest> deductRequests = order.getItems().stream()
                .map(i -> new StockUpdateRequest(
                        i.getProductId(),
                        i.getWarehouseId(),
                        i.getQuantity()))
                .toList();

        inventoryClient.deductStock(deductRequests);

        orderRepository.save(order);
        
        return order.getId();
    }
	
	public OrderResponse getOrderById(Long id) {
	    Order order = orderRepository.findById(id)
	            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
	    return mapToOrderResponse(order);
	}
	
	public Page<OrderResponse> getOrdersByCustomer(Long customerId, Pageable pageable) {
	    Page<Order> orderPage = orderRepository.findByCustomerId(customerId, pageable);
	    
	    return orderPage.map(this::mapToOrderResponse);
	}
	
	@Transactional
	public void cancelOrder(Long orderId) {
	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

	    if (order.getStatus().equals(OrderStatus.CANCELLED)) {
	        throw new StatusConflictException("Order already cancelled");
	    }

	    order.setStatus(OrderStatus.CANCELLED);
	    orderRepository.save(order);

	    List<StockUpdateRequest> request = order.getItems().stream()
	            .map(item -> new StockUpdateRequest(
	                item.getProductId(), 
	                item.getWarehouseId(), 
	                item.getQuantity()
	            ))
	            .collect(Collectors.toList());

	    inventoryClient.addStock(request);
	}
	
	public Page<OrderResponse> getAllOrders(Pageable pageable) {
	    Page<Order> orderPage = orderRepository.findAll(pageable);
	    
	    return orderPage.map(this::mapToOrderResponse);
	}
	
	private OrderResponse mapToOrderResponse(Order order) {
	    List<OrderItemResponse> items = order.getItems().stream()
	            .map(i -> new OrderItemResponse(
	            		i.getId(),
	            		i.getProductId(), 
	            		i.getQuantity(), 
	            		i.getWarehouseId(),
	            		i.getPrice(),
	            		i.getFulfillmentStatus()))
	            .collect(Collectors.toList());

	    return new OrderResponse(
	            order.getId(),
	            order.getStatus(),
	            order.getAddress(),
	            order.getTotalAmount(),
	            items,
	            order.getCreatedAt()
	    );
	}
    
}
