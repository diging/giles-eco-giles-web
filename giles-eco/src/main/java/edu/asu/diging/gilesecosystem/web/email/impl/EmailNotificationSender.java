package edu.asu.diging.gilesecosystem.web.email.impl;

import java.util.Properties;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;

public class EmailNotificationSender {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ISystemMessageHandler messageHandler;

    private boolean enabled;

    private String fromAddress;

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Resource(name = "uiMessages")
    private Properties emailMessages;

    public void sendNotificationEmail(String emailaddress, String subject, String msgText) {
        if (enabled) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(new InternetAddress(emailaddress));
                helper.setSubject(subject);
                helper.setFrom(new InternetAddress(fromAddress));

                // Adding tail to the message text
                msgText += emailMessages.getProperty("email.tail");

                helper.setText(msgText);
                mailSender.send(message);
                logger.debug("Send email to " + emailaddress + " with subject \"" + subject + "\"");
            } catch (MessagingException ex) {
                messageHandler.handleMessage("Notification email could not be sent.", ex, MessageType.ERROR);
            }
        }
    }
}
