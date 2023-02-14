package edu.asu.diging.gilesecosystem.web.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import edu.asu.diging.gilesecosystem.web.core.email.impl.NotSetupMailSender;

@Configuration
@PropertySource("classpath:config.properties")
public class EmailConfig {

    @Value("${email.username}")
    private String emailUser;

    @Value("${email.password}")
    private String emailPassword;

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private String emailPort;
    
    @Value("${email.transport.protocol}")
    private String emailTransportProtocol;
    
    @Value("${email.smtp.auth}")
    private String emailSmtpAuth;
    
    @Value("${email.smtp.starttls.enable}")
    private String emailStartTls;

    @Value("${email.debug}")
    private String emailDebug;
    
    @Bean
    public JavaMailSender javaMailSender() {
        if (emailHost == null || emailHost.isEmpty()) {
            return new NotSetupMailSender();
        }
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(emailHost);
        sender.setPort(new Integer(emailPort));
        sender.setPassword(emailPassword);
        sender.setUsername(emailUser);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.transport.protocol", emailTransportProtocol);
        javaMailProperties.put("mail.smtp.auth", false);
        javaMailProperties.put("mail.smtp.starttls.enable", emailStartTls);
        javaMailProperties.put("mail.debug", emailDebug);
        sender.setJavaMailProperties(javaMailProperties);

        return sender;
    }
    
}
