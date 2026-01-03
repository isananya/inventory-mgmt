package com.chubb.inventoryapp.dto;

import java.time.LocalDateTime;

import com.chubb.inventoryapp.model.PaymentMode;
import com.chubb.inventoryapp.model.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
	private Long id;
    private Long orderId;
    private Long customerId;
    private float totalAmount;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}
