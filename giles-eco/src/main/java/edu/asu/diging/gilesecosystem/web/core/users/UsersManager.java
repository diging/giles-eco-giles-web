package edu.asu.diging.gilesecosystem.web.core.users;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.email.IEmailNotificationManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesNotificationException;

/**
 * Managing class for user management.
 * 
 * @author Julia Damerow
 * 
 */
@Service
public class UsersManager implements IUserManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserDatabaseClient client;

    @Autowired
    private IEmailNotificationManager emailManager;

    @Autowired
    private ISystemMessageHandler messageHandler;
    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#findUser(java.lang.String)
     */
    @Override
    public User findUser(String name) {
        User user = client.findUser(name);
        return user;
    }
    
    @Override
    public User findUserByProviderUserId(String userId, String provider) {
        return client.findUserByProviderUserId(userId, provider);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#getUser(java.lang.String,
     * java.lang.String)
     */
    @Override
    public User getUser(String name, String pw) {
        User user = client.getUser(name, pw);
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        List<User> users = client.findUsersByEmail(email);
        if (users.size() > 0)
            return users.get(0);
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#getAllUsers()
     */
    @Override
    public User[] getAllUsers() {
        return client.getAllUser();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.conceptpower.users.IUserManager#addUser(edu.asu.conceptpower.
     * users.User)
     */
    @Override
    public User addUser(User user) throws UnstorableObjectException {
        client.addUser(user);
        //send notification to all admins
        List<User> adminList = client.getUsersByRole(GilesRole.ROLE_ADMIN.name());
        for(User admin : adminList) {
            try {
                emailManager.sendAccountCreatedEmail(user.getName(), user.getUsername(), admin.getFullname(), admin.getEmail());
            } catch (GilesNotificationException e) {
                // let's log warning but keep on going
                messageHandler.handleMessage("Email to " + admin.getUsername() + " could not be sent.", e, MessageType.WARNING);
            }
        }
        return user;
    }

    @Override
    public void updatePasswordEncryption(String username) {
        User user = client.findUser(username);
        client.update(user);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#deleteUser(java.lang.String)
     */
    @Override
    public void deleteUser(String username) {
        client.deleteUser(username);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.conceptpower.users.IUserManager#storeModifiedUser(edu.asu.
     * conceptpower.users.User)
     */
    @Override
    public void storeModifiedUser(User user) {
        client.update(user);
    }

    /**
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.users.conceptpower.users.IUserManager#storeModifiedPassword(edu.asu.conceptpower.users.User)
     */
    @Override
    public void storeModifiedPassword(User user) {
        client.update(user);
    }

    @Override
    public void approveUserAccount(String username) {
        User user = findUser(username);
        user.setAccountStatus(AccountStatus.APPROVED);
        if (!user.getRoles().contains(GilesRole.ROLE_USER.name())) {
            user.getRoles().add(GilesRole.ROLE_USER.name());
        }
        client.update(user);
        try {
            if (user.getEmail() != null && !user.getEmail().equals("")) {
                System.out.println("Sending mail to " + user.getEmail());
                emailManager.sendAccountApprovalOrRevokeEmail(user.getName(), user.getUsername(), user.getEmail(), true);
            }
        } catch (GilesNotificationException e) {
            // let's log warning but keep on going
            messageHandler.handleMessage("Email to " + user.getUsername() + " could not be sent.", e, MessageType.WARNING);
        }
        
    }
    
    @Override
    public void revokeUserAccount(String username) {
        User user = findUser(username);
        user.setAccountStatus(AccountStatus.REVOKED);
        user.setRoles(new ArrayList<String>());
        client.update(user);
        try {
            if (user.getEmail() != null && !user.getEmail().equals("")) {
                System.out.println("Sending mail to " + user.getEmail());
                emailManager.sendAccountApprovalOrRevokeEmail(user.getName(), user.getUsername(), user.getEmail(), false);
            }
        } catch (GilesNotificationException e) {
            // let's log warning but keep on going
            messageHandler.handleMessage("Email to " + user.getUsername() + " could not be sent.", e, MessageType.WARNING);
        }
        
    }
    
    @Override
    public void addRoleToUser(String username, GilesRole role) {
        User user = findUser(username);
        user.getRoles().add(role.name());
        client.update(user);
    }
    
    @Override
    public void removeRoleFromUser(String username, GilesRole role) {
        User user = findUser(username);
        user.getRoles().remove(role.name());
        client.update(user);
    }
    
    @Override
    public String getUniqueUsername(String providerId) {
        String id = null;
        while (true) {
            id = "USR" + client.generateUniqueId() + "_" + providerId;
            Object existingFile = findUser(id);
            if (existingFile == null) {
                break;
            }
        }
        return id;
    }
    
}