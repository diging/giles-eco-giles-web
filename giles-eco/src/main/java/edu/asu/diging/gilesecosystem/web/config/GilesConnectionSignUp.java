package edu.asu.diging.gilesecosystem.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.users.User;

public class GilesConnectionSignUp implements ConnectionSignUp {
    
    @Autowired
    private ISystemMessageHandler messageHandler;

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
            messageHandler.handleMessage("Could not store user.", e, MessageType.ERROR);
            return null;
        }
        return user.getUsername();
    }

}