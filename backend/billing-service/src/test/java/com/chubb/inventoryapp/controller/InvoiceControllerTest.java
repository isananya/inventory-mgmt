package com.chubb.inventoryapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.chubb.inventoryapp.dto.InvoiceRequest;
import com.chubb.inventoryapp.dto.InvoiceResponse;
import com.chubb.inventoryapp.dto.InvoiceStatusRequest;
import com.chubb.inventoryapp.model.PaymentMode;
import com.chubb.inventoryapp.model.PaymentStatus;
import com.chubb.inventoryapp.service.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private InvoiceResponse invoiceResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(invoiceController).build();

        invoiceResponse = new InvoiceResponse(
                1L, 100L, 1L, 500.0f, 
                PaymentMode.CARD, PaymentStatus.PAID, LocalDateTime.now()
        );
    }

    @Test
    void generateInvoice_Success() throws Exception {
        InvoiceRequest request = new InvoiceRequest();
        request.setPaymentMode(PaymentMode.UPI);
        request.setEmail("user@gmail.com");

        when(invoiceService.generateInvoice(eq(100L), any(InvoiceRequest.class))).thenReturn(1L);

        mockMvc.perform(post("/billing/order/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }
    
    @Test
    void generateInvoice_ValidationFailure() throws Exception {
        InvoiceRequest request = new InvoiceRequest();
        
        mockMvc.perform(post("/billing/order/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInvoiceByOrder_Success() throws Exception {
        when(invoiceService.getInvoiceByOrderId(100L)).thenReturn(invoiceResponse);

        mockMvc.perform(get("/billing/order/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(100L))
                .andExpect(jsonPath("$.paymentMode").value("CARD"));
    }

    @Test
    void getInvoice_Success() throws Exception {
        when(invoiceService.getInvoice(1L)).thenReturn(invoiceResponse);

        mockMvc.perform(get("/billing/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateStatus_Success() throws Exception {
        InvoiceStatusRequest request = new InvoiceStatusRequest();
        request.setStatus(PaymentStatus.PAID);

        doNothing().when(invoiceService).updateInvoiceStatus(eq(100L), eq(PaymentStatus.PAID));

        mockMvc.perform(patch("/billing/order/100/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}