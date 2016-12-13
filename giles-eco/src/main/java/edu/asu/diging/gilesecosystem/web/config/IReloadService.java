package edu.asu.diging.gilesecosystem.web.config;

import edu.asu.diging.gilesecosystem.web.exceptions.FactoryDoesNotExistException;

public interface IReloadService {
    
    public final static String GOOGLE = "GOOGLE";
    public final static String GITHUB = "GITHUB";
    public final static String MITREID = "MITREID";

    public abstract void addFactory(String factoryName, IAdjustableConnectionFactory factory);

    public abstract void updateFactory(String factoryName, String clientId, String secret)
            throws FactoryDoesNotExistException;

    public abstract void updateFactory(String factoryName, String clientId, String secret,
            String serverUrl) throws FactoryDoesNotExistException;

}