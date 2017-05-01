package edu.asu.diging.gilesecosystem.web.email.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.email.IEmailNotificationManager;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesNotificationException;
import edu.asu.diging.gilesecosystem.web.velocity.IVelocityBuilder;

@Service
public class EmailNotificationManager implements IEmailNotificationManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmailNotificationSender emailSender;

    @Autowired
    private IVelocityBuilder velocityBuilder;

    @Resource(name = "uiMessages")
    private Properties emailMessages;

    @Override
    public void sendAccountCreatedEmail(String name, String username, String adminName, String adminEmail)
            throws GilesNotificationException {
        Map<String, Object> contextProperties = new HashMap<String, Object>();

        contextProperties.put("createdUser", name);
        contextProperties.put("createdUsername", username);
        contextProperties.put("admin", adminName);

        try {
            String msg = velocityBuilder.getRenderedTemplate("velocitytemplates/email/newAccount.vm",
                    contextProperties);
            emailSender.sendNotificationEmail(adminEmail, emailMessages.getProperty("email.account_created.subject"),
                    msg);
        } catch (ResourceNotFoundException e) {
            throw new GilesNotificationException(e);
        } catch (ParseErrorException e) {
            throw new GilesNotificationException(e);
        } catch (Exception e) {
            throw new GilesNotificationException(e);
        }
    }

}
