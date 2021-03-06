package edu.asu.diging.gilesecosystem.web.core.tokens.impl;

import edu.asu.diging.gilesecosystem.web.core.tokens.IAppToken;

public class AppToken implements IAppToken {

    private String id;
    private String token;
    private String appId;
    private String providerId;
    private String authorizationType;
    
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IAppToken#getId()
     */
    @Override
    public String getId() {
        return id;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IAppToken#setId(java.lang.String)
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IAppToken#getToken()
     */
    @Override
    public String getToken() {
        return token;
    }
    /* (non-Javadoc)
     * @see edu.asu.giles.apps.impl.IAppToken#setToken(java.lang.String)
     */
    @Override
    public void setToken(String token) {
        this.token = token;
    }
    @Override
    public String getAppId() {
        return appId;
    }
    @Override
    public void setAppId(String appId) {
        this.appId = appId;
    }
    @Override
    public String getProviderId() {
        return providerId;
    }
    @Override
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
    @Override
    public String getAuthorizationType() {
        return authorizationType;
    }
    @Override
    public void setAuthorizationType(String authorizationType) {
        this.authorizationType = authorizationType;
    }
    
    
}
