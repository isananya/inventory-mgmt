package com.chubb.inventoryapp.dto;

import com.chubb.inventoryapp.model.PaymentMode;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceRequest {
    @NotNull
    private PaymentMode paymentMode;
}
