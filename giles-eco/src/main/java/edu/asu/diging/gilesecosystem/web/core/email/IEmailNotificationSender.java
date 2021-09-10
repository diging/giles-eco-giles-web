package edu.asu.diging.gilesecosystem.web.core.email;

/**
 * The purpose of this interface is to send email notifications.
 * 
 * @author snilapwa
 */
public interface IEmailNotificationSender {
    public void sendNotificationEmail(String emailaddress, String subject, String msgText);
}
