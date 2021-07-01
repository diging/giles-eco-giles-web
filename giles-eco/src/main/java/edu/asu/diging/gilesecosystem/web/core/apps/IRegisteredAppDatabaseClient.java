package edu.asu.diging.gilesecosystem.web.core.apps;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IDatabaseClient;

public interface IRegisteredAppDatabaseClient extends IDatabaseClient<IRegisteredApp> {

    public abstract IRegisteredApp getAppById(String id);

    public abstract void storeModifiedApp(IRegisteredApp app) throws UnstorableObjectException;

    public abstract IRegisteredApp[] getAllRegisteredApps();

}