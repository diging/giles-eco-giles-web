package edu.asu.diging.gilesecosystem.web.apps.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.apps.IRegisteredAppDatabaseClient;

@Transactional
@Component
public class RegisteredAppDatabaseClient extends DatabaseClient<IRegisteredApp> implements IRegisteredAppDatabaseClient {

    @PersistenceContext(unitName="AppsPU")
    private EntityManager em;
    
    @Override
    protected String getIdPrefix() {
        return "APP";
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IRegisteredAppDatabaseClient#getAppById(java.lang.String)
     */
    @Override
    public IRegisteredApp getAppById(String id) {
        return em.find(RegisteredApp.class, id);
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
        TypedQuery<IRegisteredApp> query = em.createQuery("SELECT a FROM RegisteredApp a", IRegisteredApp.class);
        List<IRegisteredApp> results = query.getResultList();
        if (results == null) {
            return new IRegisteredApp[0];
        }
        return results.toArray(new IRegisteredApp[results.size()]);
    }

    @Override
    protected IRegisteredApp getById(String id) {
        return getAppById(id);
    }

    @Override
    protected EntityManager getClient() {
        return em;
    }

}
