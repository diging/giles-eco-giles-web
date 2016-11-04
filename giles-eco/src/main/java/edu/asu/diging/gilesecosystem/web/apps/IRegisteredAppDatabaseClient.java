package edu.asu.diging.gilesecosystem.web.apps;

import com.db4o.ObjectSet;

import edu.asu.diging.gilesecosystem.web.db4o.IDatabaseClient;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;

public interface IRegisteredAppDatabaseClient extends IDatabaseClient<IRegisteredApp> {

    public abstract IRegisteredApp getAppById(String id);

    public abstract void storeModifiedApp(IRegisteredApp app) throws UnstorableObjectException;

    public abstract IRegisteredApp[] getAllRegisteredApps();

}