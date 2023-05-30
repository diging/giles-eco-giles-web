package edu.asu.diging.gilesecosystem.web.config.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.api.util.IResponseHelper;
import edu.asu.diging.gilesecosystem.web.config.CitesphereToken;
import edu.asu.diging.gilesecosystem.web.config.IUserHelper;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.users.AccountStatus;
import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

@Service
public class UserHelper implements IUserHelper {
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private IResponseHelper responseHelper;

    /* (non-Javadoc)
     * @see edu.asu.giles.config.IUserHelper#createUser(org.springframework.social.connect.Connection)
     */
    @Override
    public User createUser(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        
        String username =  createUsername(profile.getUsername(), connection.getKey().getProviderId());
        User user = new User();
        
        // make sure someone else didn't change their username to this one
        User userWithUsername = userManager.findUser(username);
        if (userWithUsername == null) {
            user.setUsername(username);
        } else {
            user.setUsername(userManager.getUniqueUsername(connection.getKey().getProviderId()));
        }
        
        user.setFirstname(profile.getFirstName());
        user.setLastname(profile.getLastName());
        user.setName(profile.getName());
        user.setEmail(profile.getEmail());
        user.setProvider(connection.getKey().getProviderId());
        user.setUserIdOfProvider(connection.getKey().getProviderUserId());
        user.setAccountStatus(AccountStatus.ADDED);
        
        return user;
    }
    
    @Override
    public String createUsername(String username, String providerId) {
        return username + "_" + providerId;
    }

    @Override
    public boolean checkUserPermission(IDocument document, CitesphereToken citesphereToken) {
        String username = document.getUsername();
        CitesphereUser user = (CitesphereUser) citesphereToken.getPrincipal();
        String usernameInSystem = createUsername(user.getUsername(), user.getAuthorizingClient());

        return username.equals(usernameInSystem);
    }
    
    @Override
    public ResponseEntity<String> generateUnauthorizedUserResponse() {
        Map<String, String> unauthorizedMsgs = new HashMap<String, String>();
        unauthorizedMsgs.put("errorMsg", "User is not authorized to check status.");
        unauthorizedMsgs.put("errorCode", "401");
        return responseHelper.generateResponse(unauthorizedMsgs, HttpStatus.UNAUTHORIZED);
    }
}
