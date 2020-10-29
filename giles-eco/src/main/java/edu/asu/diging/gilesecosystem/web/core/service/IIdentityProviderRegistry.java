package edu.asu.diging.gilesecosystem.web.core.service;

import java.util.Map;

/**
 * 
 * this interface has methods which deals with IdentityProviderRegistry class.
 * @author snilapwa
 */
public interface IIdentityProviderRegistry {

    public abstract void addProvider(String providerId, String authorizationType);

    public abstract Map<String, String> getProviders();

    public abstract String getCheckerId(String providerId, String authorizationType);

    public abstract void addProviderTokenChecker(String providerId, String authorizationType, String checkerId);

    public abstract String getProviderName(String id);

}