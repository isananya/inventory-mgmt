package com.chubb.inventoryapp.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@GetMapping
    public ResponseEntity<Page<InvoiceResponse>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy, 
            @RequestParam(defaultValue = "desc") String direction) { 
		
        Sort sort = direction.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<InvoiceResponse> response = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(response);
    }
}
