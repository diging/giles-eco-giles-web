package edu.asu.diging.gilesecosystem.web.config.social;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.connect.GitHubAdapter;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.github.connect.GitHubServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;

import edu.asu.diging.gilesecosystem.web.config.IAdjustableConnectionFactory;


public class AdjustableGithubConnectionFactory extends OAuth2ConnectionFactory<GitHub> implements IAdjustableConnectionFactory<GitHub> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private GitHubConnectionFactory delegate;
    
    public AdjustableGithubConnectionFactory(String providerId,
            ServiceProvider<GitHub> serviceProvider, ApiAdapter<GitHub> apiAdapter) {
        super(providerId, null, null);
    }
   
    public AdjustableGithubConnectionFactory(String clientId, String clientSecret) {
        super("github", new GitHubServiceProvider(clientId, clientSecret), new GitHubAdapter());
        delegate = new GitHubConnectionFactory(clientId, clientSecret);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.config.social.IAdjustableConnectionFactory#getDelegate()
     */
    @Override
    public GitHubConnectionFactory getDelegate() {
        return delegate;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.config.social.IAdjustableConnectionFactory#update(java.lang.String, java.lang.String)
     */
    @Override
    public void update(String clientId, String clientSecret) {
        delegate = new GitHubConnectionFactory(clientId, clientSecret);
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

    
    public Connection<GitHub> createConnection(AccessGrant accessGrant) {
        return (Connection<GitHub>) getDelegate().createConnection(accessGrant);
    }

    
    public Connection<GitHub> createConnection(ConnectionData data) {
        return (Connection<GitHub>) getDelegate().createConnection(data);
    }

    
    public String getProviderId() {
        return getDelegate().getProviderId();
    }
    
}
