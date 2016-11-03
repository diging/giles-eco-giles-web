package edu.asu.giles.apps;

import java.util.List;

import edu.asu.giles.db4o.IStorableObject;

public interface IRegisteredApp extends IStorableObject {

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

}