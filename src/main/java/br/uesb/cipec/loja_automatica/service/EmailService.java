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
    public void send(String username, String to, String token){
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("loja.peticomputacao@gmail.com");
            message.setSubject("Confirme seu email");
            String messageBody = "Olá, "+username+"!\n"+
            "Obrigada por se registrar no nosso site.\n\n"+
            "Para ativar sua conta e começar a usar nossos serviços,"+
            "confirme seu e-mail clicando no link abaixo:\n"+
            "http://localhost:8080/api/auth/confirmToken?token=%s".formatted(token)+"\n"+
            "Esse link é válido por 15 minutos.\n\n"+
            "Se você não solicitou este cadastro, pode ignorar esse email.\n\n"+
            "Atenciosamente,\nEquipe PETI Computação";
            message.setText(messageBody);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("DEU RUIM NO ENVIO DO EMAIL");
        }
        
    }
}
