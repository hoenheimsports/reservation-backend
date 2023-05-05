package fr.hoenheimsports.reservation.service;


import fr.hoenheimsports.reservation.model.Reservation;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.CompletableFuture;


@Service
public class EmailService {
    private static final Logger logger= LoggerFactory.getLogger(EmailService.class);
    private JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }
   @Async
    public void sendEmail(String to, String subject, String htmlBody)  {

       try {
           logger.info("Envoie d'un email à " + to);
           MimeMessage message = mailSender.createMimeMessage();
           MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
           helper.setTo(to);
           helper.setSubject(subject);
           helper.setText(htmlBody, true);
           mailSender.send(message);
       } catch (MessagingException e) {
           SimpleMailMessage mail = new SimpleMailMessage();
           mail.setTo(to);
           mail.setSubject(subject);
           mail.setText("""
                   Bonjour,
                   
                   Votre action pour la reservation soirée année 80 a été enregistrée mais une erreur dans la génération de l'email à eu lieu.
                   
                   Vous pouvez à tous moment voir l'état de votre reservation en vous munissant du code :
                   
                   https://reservation.hoenheimsports.club/reservation/ma-reservation
                   
                   Pour toutes questions, vous pouvez répondre à cette email.
                   
                   Cordialement,
                   
                   L'équipe de la soirée année 80.
                   """);
           mailSender.send(mail);
       }
    }

    @Async
    public CompletableFuture<String> generateHtmlBody(String htmlTemplate, Reservation reservation, String... additionalVariable) {
        Context context = new Context();

        if (additionalVariable.length % 2 != 0) {
            throw new IllegalArgumentException("Number of arguments should be even");
        }
        for (int i = 0; i < additionalVariable.length; i += 2) {
            String key = additionalVariable[i];
            String value = additionalVariable[i+1];
            context.setVariable(key,value);
            // Faites quelque chose avec la paire de chaînes
            System.out.println("Key: " + key + ", Value: " + value);
        }

        context.setVariable("reservation",reservation);
        return CompletableFuture.completedFuture(this.templateEngine.process(htmlTemplate,context));
    }
}
