package com.chubb.inventoryapp.service;

import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.exception.InvoiceAlreadyExistsException;
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
}
