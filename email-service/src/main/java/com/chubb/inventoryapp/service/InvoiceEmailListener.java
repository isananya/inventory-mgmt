package com.chubb.inventoryapp.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.chubb.inventoryapp.event.InvoiceEvent;

@Service
public class InvoiceEmailListener {

    private final EmailService emailService;

    public InvoiceEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "${app.queue.invoice:invoice_queue}")
    public void handleInvoiceEvent(InvoiceEvent event) {

        if ("INVOICE_GENERATED".equals(event.getEventType())) {
            emailService.sendInvoiceEmail(event.getEmail(), event.getOrderId(), event.getAmount());
        } 
    }
}
