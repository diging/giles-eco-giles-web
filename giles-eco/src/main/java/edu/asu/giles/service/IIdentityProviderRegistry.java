package edu.asu.giles.service;

import java.util.Map;

public interface IIdentityProviderRegistry {

    public abstract void addProvider(String providerId);

    public abstract Map<String, String> getProviders();

    public abstract String getCheckerId(String providerId);

    public abstract void addProviderTokenChecker(String providerId, String checkerId);

    public abstract String getProviderName(String id);

}