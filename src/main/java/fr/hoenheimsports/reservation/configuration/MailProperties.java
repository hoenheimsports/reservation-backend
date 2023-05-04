package fr.hoenheimsports.reservation.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "email")
public class MailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
    private boolean smtpAuth;
    private boolean smtpStarttls;

    // Getters and setters

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public boolean isSmtpStarttls() {
        return smtpStarttls;
    }

    public void setSmtpStarttls(boolean smtpStarttls) {
        this.smtpStarttls = smtpStarttls;
    }


}


