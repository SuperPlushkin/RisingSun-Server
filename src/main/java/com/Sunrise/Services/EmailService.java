package com.Sunrise.Services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String subject = "Подтверждение регистрации";
        String confirmationUrl = "https://your-domain.com/api/auth/confirm?token=" + token;
        String message = "Здравствуйте!\n\n" +
                "Для подтверждения регистрации перейдите по ссылке:\n" +
                confirmationUrl + "\n\n" +
                "Ссылка действительна 24 часа.";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }
}
