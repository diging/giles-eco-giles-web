package edu.asu.diging.gilesecosystem.web.email;

import edu.asu.diging.gilesecosystem.web.exceptions.GilesNotificationException;

/**
 * The purpose of this interface is to manage all the outgoing mails in the
 * system.
 * 
 * @author snilapwa
 */
public interface IEmailNotificationManager {

    /**
     * Send new account creation email notification to admin
     *
     * @param name name of new user
     * @param username username of new user
     * @param adminName name of email recipient admin
     * @param adminEmail email id of recipient admin
     * @throws GilesNotificationException
     */
    void sendAccountCreatedEmail(String name, String username, String adminName, String adminEmail)
            throws GilesNotificationException;
}
