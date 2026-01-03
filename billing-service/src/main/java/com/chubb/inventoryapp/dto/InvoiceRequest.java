package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.PaymentMode;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InvoiceRequest {
	@NotNull
	private Long orderId;
    
    @NotNull
    private Long customerId;

    @Min(0)
    private float totalAmount;
    
    @NotNull
    private PaymentMode paymentMode;
}
