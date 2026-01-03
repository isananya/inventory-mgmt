package com.chubb.inventoryapp.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.InvoiceResponse;
import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.exception.InvoiceAlreadyExistsException;
import com.chubb.inventoryapp.exception.InvoiceNotFoundException;
import com.chubb.inventoryapp.feign.OrderClientWrapper;
import com.chubb.inventoryapp.model.Invoice;
import com.chubb.inventoryapp.model.PaymentMode;
import com.chubb.inventoryapp.model.PaymentStatus;
import com.chubb.inventoryapp.repository.InvoiceRepository;

@Service
public class InvoiceService {
	
	private final InvoiceRepository invoiceRepository;
    private final OrderClientWrapper orderClient;
    
	public InvoiceService(InvoiceRepository invoiceRepository, OrderClientWrapper orderClient) {
		super();
		this.invoiceRepository = invoiceRepository;
		this.orderClient = orderClient;
	}

	public Long generateInvoice(Long orderId, PaymentMode paymentMode) {
		if (invoiceRepository.findByOrderId(orderId).isPresent()) {
	        throw new InvoiceAlreadyExistsException("Invoice already exists for Order ID: " + orderId);
	    }

        OrderResponse order = orderClient.getOrderById(orderId);

        Invoice invoice = new Invoice();
        invoice.setOrderId(order.getOrderId());
        invoice.setCustomerId(order.getCustomerId());
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setPaymentMode(paymentMode);
        invoice.setCreatedAt(LocalDateTime.now());

        if ( "CANCELLED".equalsIgnoreCase(order.getStatus())){
        	invoice.setPaymentStatus(PaymentStatus.REFUNDED);
        }
        else {
        	if (paymentMode.equals(PaymentMode.COD)) {
        		invoice.setPaymentStatus(PaymentStatus.PENDING);
        	}
        	else {
        		invoice.setPaymentStatus(PaymentStatus.PAID);
        	}
        }

        invoiceRepository.save(invoice);
        
        return invoice.getId();
    }
	
	public InvoiceResponse getInvoiceByOrderId(Long orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found for Order ID: " + orderId));
        
        return mapToResponse(invoice);
    }
	
	public InvoiceResponse getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found for ID: " + id));
      
        return mapToResponse(invoice);
    }
	
	public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        Page<Invoice> invoicePage = invoiceRepository.findAll(pageable);
        
        return invoicePage.map(this::mapToResponse);
    }
	
	private InvoiceResponse mapToResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getOrderId(),
                invoice.getCustomerId(),
                invoice.getTotalAmount(),
                invoice.getPaymentMode(),
                invoice.getPaymentStatus(),
                invoice.getCreatedAt()
        );
    }
}
