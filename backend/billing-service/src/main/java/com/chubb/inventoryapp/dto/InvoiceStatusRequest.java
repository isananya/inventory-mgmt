package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceStatusRequest {
    @NotNull(message = "Status is required")
    private PaymentStatus status;
}
