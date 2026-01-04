package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.chubb.inventoryapp.dto.InvoiceRequest;
import com.chubb.inventoryapp.dto.InvoiceResponse;
import com.chubb.inventoryapp.dto.OrderResponse;
import com.chubb.inventoryapp.event.InvoiceEventPublisher;
import com.chubb.inventoryapp.exception.InvoiceAlreadyExistsException;
import com.chubb.inventoryapp.exception.InvoiceNotFoundException;
import com.chubb.inventoryapp.feign.OrderClientWrapper;
import com.chubb.inventoryapp.model.Invoice;
import com.chubb.inventoryapp.model.PaymentMode;
import com.chubb.inventoryapp.model.PaymentStatus;
import com.chubb.inventoryapp.repository.InvoiceRepository;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private OrderClientWrapper orderClient;

    @Mock
    private InvoiceEventPublisher eventPublisher;

    @InjectMocks
    private InvoiceService invoiceService;

    private InvoiceRequest invoiceRequest;
    private OrderResponse orderResponse;
    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoiceRequest = new InvoiceRequest();
        invoiceRequest.setEmail("test@gmail.com");
        invoiceRequest.setPaymentMode(PaymentMode.CARD);

        orderResponse = new OrderResponse();
        orderResponse.setOrderId(100L);
        orderResponse.setCustomerId(1L);
        orderResponse.setTotalAmount(500.0f);
        orderResponse.setStatus("CREATED");

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setOrderId(100L);
        invoice.setCustomerId(1L);
        invoice.setTotalAmount(500.0f);
        invoice.setPaymentMode(PaymentMode.CARD);
        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void generateInvoice_Success_Paid() {
        when(invoiceRepository.findByOrderId(100L)).thenReturn(Optional.empty());
        when(orderClient.getOrderById(100L)).thenReturn(orderResponse);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> {
            Invoice inv = i.getArgument(0);
            inv.setId(1L);
            return inv;
        });
        doNothing().when(eventPublisher).publishInvoiceGenerated(anyLong(), anyString(), anyFloat());

        Long invoiceId = invoiceService.generateInvoice(100L, invoiceRequest);

        assertEquals(1L, invoiceId);
        verify(invoiceRepository).save(any(Invoice.class));
        verify(eventPublisher).publishInvoiceGenerated(eq(100L), eq("test@gmail.com"), eq(500.0f));
    }

    @Test
    void generateInvoice_Success_COD_Pending() {
        invoiceRequest.setPaymentMode(PaymentMode.COD);
        
        when(invoiceRepository.findByOrderId(100L)).thenReturn(Optional.empty());
        when(orderClient.getOrderById(100L)).thenReturn(orderResponse);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        invoiceService.generateInvoice(100L, invoiceRequest);

        verify(invoiceRepository).save(argThat(inv -> 
            inv.getPaymentStatus() == PaymentStatus.PENDING && 
            inv.getPaymentMode() == PaymentMode.COD
        ));
    }

    @Test
    void generateInvoice_OrderCancelled_Refunded() {
        orderResponse.setStatus("CANCELLED");

        when(invoiceRepository.findByOrderId(100L)).thenReturn(Optional.empty());
        when(orderClient.getOrderById(100L)).thenReturn(orderResponse);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        invoiceService.generateInvoice(100L, invoiceRequest);

        verify(invoiceRepository).save(argThat(inv -> 
            inv.getPaymentStatus() == PaymentStatus.REFUNDED
        ));
    }

    @Test
    void generateInvoice_AlreadyExists_ThrowsException() {
        when(invoiceRepository.findByOrderId(100L)).thenReturn(Optional.of(invoice));

        assertThrows(InvoiceAlreadyExistsException.class, () -> invoiceService.generateInvoice(100L, invoiceRequest));
        
        verify(orderClient, never()).getOrderById(anyLong());
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void getInvoiceByOrderId_Success() {
        when(invoiceRepository.findByOrderId(100L)).thenReturn(Optional.of(invoice));

        InvoiceResponse response = invoiceService.getInvoiceByOrderId(100L);

        assertEquals(100L, response.getOrderId());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());
    }

    @Test
    void getInvoiceByOrderId_NotFound_ThrowsException() {
        when(invoiceRepository.findByOrderId(99L)).thenReturn(Optional.empty());
        assertThrows(InvoiceNotFoundException.class, () -> invoiceService.getInvoiceByOrderId(99L));
    }

    @Test
    void getInvoice_Success() {
        when(invoiceRepository.findById(1L)).thenReturn(Optional.of(invoice));

        InvoiceResponse response = invoiceService.getInvoice(1L);

        assertEquals(1L, response.getId());
    }
    
    @Test
    void getAllInvoices_Success() {
        Page<Invoice> page = new PageImpl<>(Collections.singletonList(invoice));
        when(invoiceRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<InvoiceResponse> result = invoiceService.getAllInvoices(Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        assertEquals(100L, result.getContent().get(0).getOrderId());
    }

    @Test
    void updateInvoiceStatus_Success() {
        when(invoiceRepository.findByOrderId(100L)).thenReturn(Optional.of(invoice));

        invoiceService.updateInvoiceStatus(100L, PaymentStatus.PAID);

        assertEquals(PaymentStatus.PAID, invoice.getPaymentStatus());
        verify(invoiceRepository).save(invoice);
    }

    @Test
    void updateInvoiceStatus_NotFound_ThrowsException() {
        when(invoiceRepository.findByOrderId(99L)).thenReturn(Optional.empty());
        assertThrows(InvoiceNotFoundException.class, () -> invoiceService.updateInvoiceStatus(99L, PaymentStatus.PAID));
    }
}