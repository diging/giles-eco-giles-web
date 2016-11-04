package edu.asu.diging.gilesecosystem.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.users.User;

public class GilesConnectionSignUp implements ConnectionSignUp {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IUserManager userManager;
    private IUserHelper userHelper;

    public GilesConnectionSignUp(IUserManager userManager, IUserHelper userHelper) {
        this.userManager = userManager;
        this.userHelper = userHelper;
    }
 
    public String execute(Connection<?> connection) {
        
        User user = userHelper.createUser(connection);
        
        try {
            userManager.addUser(user);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store user.", e);
            return null;
        }
        return user.getUsername();
    }

}