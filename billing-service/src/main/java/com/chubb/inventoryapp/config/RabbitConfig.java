package com.chubb.inventoryapp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${app.queue.invoice}")
    private String invoiceQueueName;

    @Value("${app.queue.exchange}")
    private String exchangeName;

    @Value("${app.queue.routing.generated}")
    private String generatedRoutingKey;

    @Bean
    public Queue invoiceQueue() {
        return new Queue(invoiceQueueName, true);
    }

    @Bean
    public DirectExchange invoiceExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding invoiceGeneratedBinding(Queue invoiceQueue, DirectExchange invoiceExchange) {
        return BindingBuilder
                .bind(invoiceQueue)
                .to(invoiceExchange)
                .with(generatedRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
