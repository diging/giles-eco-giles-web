package edu.asu.giles.apps;

import com.db4o.ObjectSet;

import edu.asu.giles.db4o.IDatabaseClient;
import edu.asu.giles.exceptions.UnstorableObjectException;

public interface IRegisteredAppDatabaseClient extends IDatabaseClient<IRegisteredApp> {

    public abstract IRegisteredApp getAppById(String id);

    public abstract void storeModifiedApp(IRegisteredApp app) throws UnstorableObjectException;

    public abstract IRegisteredApp[] getAllRegisteredApps();

}