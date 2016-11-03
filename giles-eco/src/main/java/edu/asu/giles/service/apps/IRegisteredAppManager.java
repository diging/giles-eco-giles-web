package edu.asu.giles.service.apps;

import java.util.ArrayList;
import java.util.List;

import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.exceptions.TokenGenerationErrorException;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.tokens.IAppToken;

public interface IRegisteredAppManager {

    public abstract IRegisteredApp storeApp(IRegisteredApp app);

    public abstract List<IRegisteredApp> getRegisteredApps();

    public abstract IAppToken createToken(IRegisteredApp app) throws TokenGenerationErrorException;

    public abstract IRegisteredApp getApp(String id);

    public abstract void deleteApp(String id);

}