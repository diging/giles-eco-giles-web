package edu.asu.giles.aspects.access.tokens.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.aspects.access.openid.google.Checker;
import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.service.apps.IRegisteredAppManager;
import edu.asu.giles.service.properties.IPropertiesManager;

@Service
public class GoogleChecker implements IChecker {
    
    public final static String ID = "GOOGLE";
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    @Autowired
    private IRegisteredAppManager appsManager;
    
    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CheckerResult validateToken(String token, String appId) throws GeneralSecurityException, IOException, InvalidTokenException {
        IRegisteredApp app = appsManager.getApp(appId);
        String[] clientIdsList = new String[] { app.getProviderClientId() };
        
        Checker checker = new Checker(clientIdsList, clientIdsList);
        
        try {
            return checker.check(token);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("The provided token is not a valid Google OpenId token.", e);
        }
    }

}
