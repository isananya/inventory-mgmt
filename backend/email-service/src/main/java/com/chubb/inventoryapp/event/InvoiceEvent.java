package com.chubb.inventoryapp.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEvent implements Serializable {
    private String email;
    private Long orderId;
    private float amount;
    private String eventType;
}
