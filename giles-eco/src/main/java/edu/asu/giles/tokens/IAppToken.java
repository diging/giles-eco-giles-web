package edu.asu.giles.tokens;

public interface IAppToken extends ITokenContents {

    public abstract String getId();

    public abstract void setId(String id);

    public abstract String getToken();

    public abstract void setToken(String token);

    public abstract void setProviderId(String providerId);

    public abstract String getProviderId();

    public abstract void setAppId(String appId);

    public abstract String getAppId();

}