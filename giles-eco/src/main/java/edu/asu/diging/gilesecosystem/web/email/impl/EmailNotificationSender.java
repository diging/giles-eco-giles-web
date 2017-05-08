package edu.asu.diging.gilesecosystem.web.email.impl;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.email.IEmailNotificationSender;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class EmailNotificationSender implements IEmailNotificationSender {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private IPropertiesManager propertyManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Autowired
    private MessageSource emailMessages;

    public void sendNotificationEmail(String emailaddress, String subject, String msgText) {
        boolean enabled = (propertyManager.getProperty(Properties.EMAIL_ENABLED)).equals("true");
        String fromAddress= propertyManager.getProperty(Properties.EMAIL_FROM);
        if (enabled) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(new InternetAddress(emailaddress));
                helper.setSubject(subject);
                helper.setFrom(new InternetAddress(fromAddress));

                // Adding tail to the message text
                msgText += emailMessages.getMessage("email.tail", new String[]{}, null);

                helper.setText(msgText);
                mailSender.send(message);
                logger.debug("Send email to " + emailaddress + " with subject \"" + subject + "\"");
            } catch (MessagingException ex) {
                messageHandler.handleMessage("Notification email could not be sent.", ex, MessageType.ERROR);
            }
        }
    }
}
