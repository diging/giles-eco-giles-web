package edu.asu.diging.gilesecosystem.web.config.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.UserProfile;
import org.springframework.stereotype.Service;

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
    public boolean isUserPermittedToAccessDocument(IDocument document, CitesphereToken citesphereToken) {
        String username = document.getUsername();
        CitesphereUser user = (CitesphereUser) citesphereToken.getPrincipal();
        String usernameInSystem = createUsername(user.getUsername(), user.getAuthorizingClient());

        return username.equals(usernameInSystem);
    }
}
