package com.chubb.inventoryapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendInvoiceEmail_Success() {
        String toEmail = "test@example.com";
        Long orderId = 12345L;
        float amount = 500.0f;

        emailService.sendInvoiceEmail(toEmail, orderId, amount);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Order Successful - Order #12345", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("Total Amount: Rs500.0"));
        assertTrue(sentMessage.getText().contains("Order ID: 12345"));
    }
}