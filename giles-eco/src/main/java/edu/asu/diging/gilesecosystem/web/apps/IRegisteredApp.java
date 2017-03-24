package edu.asu.diging.gilesecosystem.web.apps;

import java.util.List;

import edu.asu.diging.gilesecosystem.util.store.IStorableObject;

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

    /**
     * authorization type required to use in token validation.
     * @return empty string for IdToken validation
     * @return non-empty string for accessToken validation
     */
    public abstract String getAuthorizationType();

    public abstract void setAuthorizationType(String authorizationType);

}