package edu.asu.diging.gilesecosystem.web.core.service.apps.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.core.apps.IRegisteredAppDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.exceptions.TokenGenerationErrorException;
import edu.asu.diging.gilesecosystem.web.core.service.apps.IRegisteredAppManager;
import edu.asu.diging.gilesecosystem.web.core.tokens.IAppToken;
import edu.asu.diging.gilesecosystem.web.core.tokens.ITokenService;

@Transactional("transactionManager")
@Service
public class RegisteredAppsManager implements IRegisteredAppManager {

    @Autowired
    private IRegisteredAppDatabaseClient databaseClient;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private ISystemMessageHandler messageHandler;

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

        String[] providerId = app.getProviderId().split("_");
        if(providerId.length > 1) {
            app.setProviderId(providerId[0]);
            app.setAuthorizationType(providerId[1]);
        } else {
            app.setAuthorizationType("");
        }

        try {
            databaseClient.storeRegisteredApp(app);
        } catch (UnstorableObjectException e) {
            messageHandler.handleMessage("Could not store app.", e, MessageType.ERROR);
            return null;
        } catch (IllegalArgumentException e) {
            messageHandler.handleMessage("Could not store app.", e, MessageType.ERROR);
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
        databaseClient.deleteRegisteredApp(app);
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
        } catch (IllegalArgumentException e) {
            throw new TokenGenerationErrorException("Token was generated but app could not be stored.", e);
        }
        
        return token;
    }
}
