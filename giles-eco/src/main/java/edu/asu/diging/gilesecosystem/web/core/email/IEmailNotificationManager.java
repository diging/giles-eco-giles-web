package edu.asu.diging.gilesecosystem.web.core.email;

import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesNotificationException;

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
    
    void sendAccountApprovalOrRevokeEmail(String name, String userName, String userEmail, boolean approved) throws GilesNotificationException;
}
