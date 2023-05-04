package fr.hoenheimsports.reservation.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfiguration {
    private MailProperties emailProperties;

    public MailConfiguration(MailProperties emailProperties) {
        this.emailProperties = emailProperties;
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailProperties.getHost());
        mailSender.setPort(emailProperties.getPort());

        mailSender.setUsername(emailProperties.getUsername());
        mailSender.setPassword(emailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", emailProperties.getProtocol());
        props.put("mail.smtp.auth", emailProperties.isSmtpAuth());
        props.put("mail.smtp.starttls.enable", emailProperties.isSmtpStarttls());

        return mailSender;
    }
}
