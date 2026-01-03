package com.chubb.inventoryapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chubb.inventoryapp.dto.InvoiceRequest;
import com.chubb.inventoryapp.dto.InvoiceResponse;
import com.chubb.inventoryapp.service.InvoiceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/billing")
public class InvoiceController {
	
	private final InvoiceService invoiceService;

	public InvoiceController(InvoiceService invoiceService) {
		super();
		this.invoiceService = invoiceService;
	}

	@PostMapping("/order/{orderId}")
    public ResponseEntity<Long> generateInvoice(@PathVariable Long orderId,
    		@RequestBody @Valid InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.generateInvoice(orderId, request.getPaymentMode()));
    }
	
	@GetMapping("/order/{orderId}")
    public ResponseEntity<InvoiceResponse> getInvoiceByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(invoiceService.getInvoiceByOrderId(orderId));
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }
}
