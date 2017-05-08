package edu.asu.diging.gilesecosystem.web.config.social;


import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.mitreidconnect.api.MitreidConnect;
import org.springframework.social.mitreidconnect.connect.MitreidConnectAdapter;
import org.springframework.social.mitreidconnect.connect.MitreidConnectConnectionFactory;
import org.springframework.social.mitreidconnect.connect.MitreidConnectServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;

import edu.asu.diging.gilesecosystem.web.config.IAdjustableConnectionFactory;


public class AdjustableMitreidConnectionFactory extends OAuth2ConnectionFactory<MitreidConnect> implements IAdjustableConnectionFactory<MitreidConnect> {

    private MitreidConnectConnectionFactory delegate;
    private String providerUrl;
    
    public AdjustableMitreidConnectionFactory(String providerId,
            ServiceProvider<GitHub> serviceProvider, ApiAdapter<GitHub> apiAdapter) {
        super(providerId, null, null);
    }
   
    public AdjustableMitreidConnectionFactory(String clientId, String clientSecret, String providerUrl) {
        super("mitreidconnect", new MitreidConnectServiceProvider(clientId, clientSecret, providerUrl), new MitreidConnectAdapter());
        delegate = new MitreidConnectConnectionFactory(clientId, clientSecret, providerUrl);
        this.providerUrl = providerUrl;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.config.social.IAdjustableConnectionFactory#getDelegate()
     */
    @Override
    public MitreidConnectConnectionFactory getDelegate() {
        return delegate;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.config.social.IAdjustableConnectionFactory#update(java.lang.String, java.lang.String)
     */
    @Override
    public void update(String clientId, String clientSecret) {
        delegate = new MitreidConnectConnectionFactory(clientId, clientSecret, providerUrl);
    }
    
    @Override
    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
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

    
    public Connection<MitreidConnect> createConnection(AccessGrant accessGrant) {
        return (Connection<MitreidConnect>) getDelegate().createConnection(accessGrant);
    }

    
    public Connection<MitreidConnect> createConnection(ConnectionData data) {
        return (Connection<MitreidConnect>) getDelegate().createConnection(data);
    }

    
    public String getProviderId() {
        return getDelegate().getProviderId();
    }
}
