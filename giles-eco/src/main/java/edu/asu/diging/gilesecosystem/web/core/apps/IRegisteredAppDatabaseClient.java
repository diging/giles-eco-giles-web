package edu.asu.diging.gilesecosystem.web.core.apps;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;

public interface IRegisteredAppDatabaseClient extends IDatabaseClient<IRegisteredApp> {

    IRegisteredApp getAppById(String id);

    void storeModifiedApp(IRegisteredApp app) throws UnstorableObjectException, IllegalArgumentException;

    IRegisteredApp[] getAllRegisteredApps();
    
    void storeRegisteredApp(IRegisteredApp app) throws UnstorableObjectException, IllegalArgumentException;
    
    void deleteRegisteredApp(IRegisteredApp app);
}
