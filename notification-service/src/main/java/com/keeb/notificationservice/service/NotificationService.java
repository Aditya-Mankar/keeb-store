package com.keeb.notificationservice.service;

import com.keeb.notificationservice.dto.OrderDto;
import com.keeb.notificationservice.model.Mail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendMail(OrderDto order, String template, String subject) {
        try {
            Map<String, Object> props = new HashMap<>();
            props.put("order", order);

            Mail mail = Mail.builder()
                    .mailFrom("test@gmail.com")
                    .mailTo(order.getOrderedBy())
                    .subject(subject)
                    .props(props)
                    .build();

            sendHtmlMail(mail, template);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendHtmlMail(Mail mail, String template) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getProps());

        String html = templateEngine.process(template, context);

        helper.setFrom(mail.getMailFrom());
        helper.setTo(mail.getMailTo());
        helper.setSubject(mail.getSubject());
        helper.setText(html, true);

        mailSender.send(message);
    }

}
