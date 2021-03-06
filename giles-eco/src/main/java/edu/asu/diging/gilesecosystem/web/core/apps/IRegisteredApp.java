package edu.asu.diging.gilesecosystem.web.core.apps;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.store.IStorableObject;

public interface IRegisteredApp extends IStorableObject {
    
    public final String PROVIDER = "GilesEcosystem";

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract List<String> getTokenIds();

    public abstract void setTokenIds(List<String> tokenIds);

    public abstract void setProviderId(String providerId);

    public abstract String getProviderId();

    public abstract void setProviderClientId(String providerClientId);

    public abstract String getProviderClientId();

    /**
     * return type of authorization used.
     */
    public abstract String getAuthorizationType();

    public abstract void setAuthorizationType(String authorizationType);

}