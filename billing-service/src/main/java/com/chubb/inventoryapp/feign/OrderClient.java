package com.chubb.inventoryapp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.chubb.inventoryapp.dto.OrderResponse;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/order/{id}")
    public OrderResponse getOrderById(@PathVariable("id") Long id);
}