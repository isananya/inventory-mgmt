package com.chubb.inventoryapp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.chubb.inventoryapp.dto.InventoryCheckResponse;
import com.chubb.inventoryapp.dto.OrderItemRequest;
import com.chubb.inventoryapp.dto.StockUpdateRequest;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryClient {

	@PostMapping("/check")
	InventoryCheckResponse checkStock(@RequestBody OrderItemRequest request);

	@PostMapping("/deduct")
	void deductStock(@RequestBody StockUpdateRequest request);

	@PostMapping("/add")
	void addStock(@RequestBody StockUpdateRequest request);
}
