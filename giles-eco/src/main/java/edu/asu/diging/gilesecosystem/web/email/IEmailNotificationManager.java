package edu.asu.diging.gilesecosystem.web.email;

import edu.asu.diging.gilesecosystem.web.exceptions.GilesNotificationException;

/**
 * The purpose of this interface is to manage all the outgoing mails in the
 * system.
 * 
 * @author snilapwa
 */
public interface IEmailNotificationManager {

    void sendAccountCreatedEmail(String name, String username, String adminName, String adminEmail)
            throws GilesNotificationException;
}
