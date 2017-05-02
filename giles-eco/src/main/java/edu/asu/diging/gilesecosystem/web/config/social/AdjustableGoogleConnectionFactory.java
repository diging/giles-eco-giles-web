package edu.asu.diging.gilesecosystem.web.config.social;


import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.connect.GoogleAdapter;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.google.connect.GoogleServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;

import edu.asu.diging.gilesecosystem.web.config.IAdjustableConnectionFactory;


public class AdjustableGoogleConnectionFactory extends OAuth2ConnectionFactory<Google> implements IAdjustableConnectionFactory<Google> {

    private GoogleConnectionFactory delegate;
    
    public AdjustableGoogleConnectionFactory(String providerId,
            ServiceProvider<Google> serviceProvider, ApiAdapter<Google> apiAdapter) {
        super(providerId, null, null);
    }
   
    public AdjustableGoogleConnectionFactory(String clientId, String clientSecret) {
        super("google", new GoogleServiceProvider(clientId, clientSecret),
                new GoogleAdapter());
        delegate = new GoogleConnectionFactory(clientId, clientSecret);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.config.social.IAdjustableConnectionFactory#getDelegate()
     */
    @Override
    public GoogleConnectionFactory getDelegate() {
        return delegate;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.config.social.IAdjustableConnectionFactory#update(java.lang.String, java.lang.String)
     */
    @Override
    public void update(String clientId, String clientSecret) {
        delegate = new GoogleConnectionFactory(clientId, clientSecret);
    }
    
    /*
     * Delegating methods 
     */
    
    public void setScope(String scope) {
        getDelegate().setScope(scope);
    }

    public String getScope() {
        return getDelegate().getScope();
    }

    public String generateState() {
        return getDelegate().generateState();
    }

    
    public boolean supportsStateParameter() {
        return getDelegate().supportsStateParameter();
    }

    
    public OAuth2Operations getOAuthOperations() {
        return getDelegate().getOAuthOperations();
    }

    
    public Connection<Google> createConnection(AccessGrant accessGrant) {
        return (Connection<Google>) getDelegate().createConnection(accessGrant);
    }

    
    public Connection<Google> createConnection(ConnectionData data) {
        return (Connection<Google>) getDelegate().createConnection(data);
    }

    
    public String getProviderId() {
        return getDelegate().getProviderId();
    }

    public void setProviderUrl(String providerUrl) {
    }
}
