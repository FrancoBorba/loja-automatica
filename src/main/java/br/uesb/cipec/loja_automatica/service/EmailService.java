package br.uesb.cipec.loja_automatica.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public final JavaMailSender mailSender;

    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(String to, String token){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("anaclarav.p.04@gmail.com");
            message.setSubject("Confirm your email");
            String messageBody = "http://localhost:8080/api/auth/confirmToken?token=%s".formatted(token);
            message.setText(messageBody);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("DEU RUIM NO ENVIO DO EMAIL");
        }
        
    }
}
