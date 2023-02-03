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

    /**
     * Send account approval email to the user
     *
     * @param name name of new user
     * @param userName userName of new user
     * @param userEmail email id of user
     * @throws GilesNotificationException
     */
    void sendAccountApprovalEmail(String name, String userName, String userEmail) throws GilesNotificationException;

    /**
     * Send account revoked email to the user
     *
     * @param name name of new user
     * @param userName userName of new user
     * @param userEmail email id of user
     * @throws GilesNotificationException
     */
    void sendAccountRevokedEmail(String name, String userName, String userEmail) throws GilesNotificationException;
    
}
