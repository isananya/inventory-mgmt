package com.chubb.inventoryapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendInvoiceEmail(String toEmail, Long orderId, float amount) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Order Successful - Order #" + orderId);
        message.setText("Thank you for your purchase!\n\n" +
                        "Order ID: " + orderId + "\n" +
                        "Total Amount: Rs" + amount + "\n\n" +
                        "Your order has been successfully placed.");
                
        mailSender.send(message);
    }
}
