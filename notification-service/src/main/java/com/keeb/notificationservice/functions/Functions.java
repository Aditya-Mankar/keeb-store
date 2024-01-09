package com.keeb.notificationservice.functions;

import com.keeb.notificationservice.dto.OrderDto;
import com.keeb.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class Functions {

    @Autowired
    private NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);

    @Bean
    public Function<OrderDto, String> sendOrderCreatedEmail() {
        return orderDto -> {
            log.info("Sending email for order creation with details : " +  orderDto.toString());

            notificationService.sendMail(orderDto, "order-placed-template", "Order placed");

            return "Email send successfully for order creation by " + orderDto.getOrderedBy();
        };
    }



}
