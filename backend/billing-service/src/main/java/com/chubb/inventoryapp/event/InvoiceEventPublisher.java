package com.chubb.inventoryapp.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InvoiceEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String generatedRoutingKey;

    public InvoiceEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.queue.exchange}") String exchange,
            @Value("${app.queue.routing.generated}") String generatedRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.generatedRoutingKey = generatedRoutingKey;
    }

    public void publishInvoiceGenerated(Long orderId, String email, float amount) {
        InvoiceEvent event = new InvoiceEvent(email, orderId, amount, "INVOICE_GENERATED");
        rabbitTemplate.convertAndSend(exchange, generatedRoutingKey, event);
    }
}
