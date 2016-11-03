package edu.asu.giles.apps.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.apps.IRegisteredAppDatabaseClient;
import edu.asu.giles.db4o.impl.DatabaseClient;
import edu.asu.giles.db4o.impl.DatabaseManager;
import edu.asu.giles.exceptions.UnstorableObjectException;

@Component
public class RegisteredAppDatabaseClient extends DatabaseClient<IRegisteredApp> implements IRegisteredAppDatabaseClient {

    private ObjectContainer client;

    @Autowired
    @Qualifier("appDatabaseManager")
    private DatabaseManager userDatabase;
    
    @PostConstruct
    public void init() {
        client = userDatabase.getClient();
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
        IRegisteredApp app = new RegisteredApp();
        app.setId(id);
        
        return queryByExampleGetFirst(app);
    }
    
    @Override
    public void storeModifiedApp(IRegisteredApp app) throws UnstorableObjectException {
        IRegisteredApp storedApp = getAppById(app.getId());
        storedApp.setName(app.getName());
        storedApp.setTokenIds(app.getTokenIds());
        store(storedApp);
    }
    
    @Override
    public IRegisteredApp[] getAllRegisteredApps() {
        ObjectSet<IRegisteredApp> results = client.query(IRegisteredApp.class);
        if (results == null) {
            return new IRegisteredApp[0];
        }
        return results.toArray(new IRegisteredApp[results.size()]);
    }

    @Override
    protected Object getById(String id) {
        return getAppById(id);
    }

    @Override
    protected ObjectContainer getClient() {
        return client;
    }

}
