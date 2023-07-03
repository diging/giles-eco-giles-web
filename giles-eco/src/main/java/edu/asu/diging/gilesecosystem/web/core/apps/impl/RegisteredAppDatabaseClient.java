package edu.asu.diging.gilesecosystem.web.core.apps.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.core.apps.IRegisteredAppDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.apps.RegisteredAppRepository;

@Transactional
@Component
public class RegisteredAppDatabaseClient extends DatabaseClient<IRegisteredApp> implements IRegisteredAppDatabaseClient {
    
    @Autowired
    private final RegisteredAppRepository registeredAppRepository;
    
    @Autowired
    public RegisteredAppDatabaseClient(RegisteredAppRepository registeredAppRepository) {
        this.registeredAppRepository = registeredAppRepository;
    }
    
    @Override
    protected String getIdPrefix() {
        return "APP";
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredAppDatabaseClient#getAppById(java.lang.String)
     */
    @Override
    public IRegisteredApp getAppById(String id) {
        return registeredAppRepository.findById(id).orElse(null);
    }
    
    @Override
    public void storeModifiedApp(IRegisteredApp app) throws UnstorableObjectException {
        if (app.getId() == null) {
            throw new UnstorableObjectException("App does not have an id.");
        }
        registeredAppRepository.save((RegisteredApp) app);
    }
    
    @Override
    public IRegisteredApp[] getAllRegisteredApps() {
        List<RegisteredApp> registeredApps = registeredAppRepository.findAll();
        if (registeredApps == null) {
            return new IRegisteredApp[0];
        }
        return registeredApps.toArray(new IRegisteredApp[registeredApps.size()]);
    }

    @Override
    protected IRegisteredApp getById(String id) {
        return getAppById(id);
    }

    @Override
    protected EntityManager getClient() {
        return null;
    }
}
