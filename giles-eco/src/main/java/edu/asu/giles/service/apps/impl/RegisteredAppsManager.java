package edu.asu.giles.service.apps.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.apps.IRegisteredAppDatabaseClient;
import edu.asu.giles.exceptions.TokenGenerationErrorException;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.service.apps.IRegisteredAppManager;
import edu.asu.giles.tokens.IAppToken;
import edu.asu.giles.tokens.ITokenService;

@Service
public class RegisteredAppsManager implements IRegisteredAppManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IRegisteredAppDatabaseClient databaseClient;

    @Autowired
    private ITokenService tokenService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.service.apps.impl.IRegisteredAppManager#storeApp(edu.asu
     * .giles.apps.IRegisteredApp)
     */
    @Override
    public IRegisteredApp storeApp(IRegisteredApp app) {

        String id = databaseClient.generateId();
        app.setId(id);

        try {
            databaseClient.store(app);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store app.", e);
            return null;
        }
        return app;
    }
    
    @Override
    public IRegisteredApp getApp(String id) {
        return databaseClient.getAppById(id);
    }
    
    @Override
    public void deleteApp(String id) {
        IRegisteredApp app = getApp(id);
        databaseClient.delete(app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.service.apps.impl.IRegisteredAppManager#getRegisteredApps()
     */
    @Override
    public List<IRegisteredApp> getRegisteredApps() {
        IRegisteredApp[] apps = databaseClient.getAllRegisteredApps();
        return Arrays.asList(apps);
    }

    @Override
    public IAppToken createToken(IRegisteredApp app) throws TokenGenerationErrorException {
        IAppToken token = tokenService.generateAppToken(app);
        
        if (app.getTokenIds() == null) {
            app.setTokenIds(new ArrayList<String>());
        }
        
        app.getTokenIds().add(token.getId());
        try {
            databaseClient.storeModifiedApp(app);
        } catch (UnstorableObjectException e) {
            throw new TokenGenerationErrorException("Token was generated but app could not be stored.", e);
        }
        
        return token;
    }
}
