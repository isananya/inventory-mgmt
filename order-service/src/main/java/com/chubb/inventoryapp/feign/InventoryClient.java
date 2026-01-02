package com.chubb.inventoryapp.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chubb.inventoryapp.dto.StockCheckResponse;
import com.chubb.inventoryapp.dto.OrderItemRequest;
import com.chubb.inventoryapp.dto.StockUpdateRequest;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryClient {

	@PostMapping("/check")
	public List<StockCheckResponse> checkStock(@RequestBody List<OrderItemRequest> request);

	@PutMapping("/stock/deduct")
	public void deductStock(@RequestBody List<StockUpdateRequest> request);

	@PutMapping("/stock/add")
	public void addStock(@RequestBody List<StockUpdateRequest> request);
}
