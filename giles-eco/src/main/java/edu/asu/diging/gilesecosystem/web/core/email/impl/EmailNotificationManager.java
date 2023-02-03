package edu.asu.diging.gilesecosystem.web.core.email.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.core.email.IEmailNotificationManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesNotificationException;
import edu.asu.diging.gilesecosystem.web.core.velocity.IVelocityBuilder;

@Service
public class EmailNotificationManager implements IEmailNotificationManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EmailNotificationSender emailSender;

    @Autowired
    private IVelocityBuilder velocityBuilder;

    @Autowired
    private MessageSource emailMessages;

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
            emailSender.sendNotificationEmail(adminEmail, emailMessages.getMessage("email.account_created.subject", new String[]{}, null),
                    msg);
        } catch (ResourceNotFoundException e) {
            throw new GilesNotificationException(e);
        } catch (ParseErrorException e) {
            throw new GilesNotificationException(e);
        } catch (Exception e) {
            throw new GilesNotificationException(e);
        }
    }
    
    @Override
    public void sendAccountApprovalEmail(String name, String userName, String userEmail) 
            throws GilesNotificationException {
        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("name", name);
        contextProperties.put("userName", userName);
        try {
            emailSender.sendNotificationEmail(userEmail, emailMessages.getMessage("email.account_approved.subject", new String[]{}, null), 
                    velocityBuilder.getRenderedTemplate("velocitytemplates/email/accountApproved.vm", contextProperties));
        } catch (ResourceNotFoundException e) {
            throw new GilesNotificationException(e);
        } catch (ParseErrorException e) {
            throw new GilesNotificationException(e);
        } catch (Exception e) {
            throw new GilesNotificationException(e);
        }
        
    }
    
    @Override
    public void sendAccountRevokedEmail(String name, String userName, String userEmail) 
            throws GilesNotificationException {
        Map<String, Object> contextProperties = new HashMap<String, Object>();
        contextProperties.put("name", name);
        contextProperties.put("userName", userName);
        try {
            emailSender.sendNotificationEmail(userEmail, emailMessages.getMessage("email.account_revoked.subject", new String[]{}, null), 
                    velocityBuilder.getRenderedTemplate("velocitytemplates/email/accountRevoked.vm", contextProperties));
        } catch (ResourceNotFoundException e) {
            throw new GilesNotificationException(e);
        } catch (ParseErrorException e) {
            throw new GilesNotificationException(e);
        } catch (Exception e) {
            throw new GilesNotificationException(e);
        }
        
    }

}
