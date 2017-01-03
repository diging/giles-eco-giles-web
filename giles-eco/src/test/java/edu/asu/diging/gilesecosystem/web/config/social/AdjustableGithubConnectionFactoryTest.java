package edu.asu.diging.gilesecosystem.web.config.social;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;

public class AdjustableGithubConnectionFactoryTest {

    @Mock private GitHubConnectionFactory delegate;
    
    @InjectMocks private AdjustableGithubConnectionFactory factoryToTest;
    
    private final String CLIENT_ID = "clientId";
    private final String CLIENT_SECRET = "secret";
    
    @Before
    public void setUp() {
        factoryToTest = new AdjustableGithubConnectionFactory(CLIENT_ID, CLIENT_SECRET);
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_update() {
        GitHubConnectionFactory previousFactory = factoryToTest.getDelegate();
        factoryToTest.update("clientId2", "secret2");
        GitHubConnectionFactory newFactory = factoryToTest.getDelegate();
        Assert.assertNotSame(previousFactory, newFactory);
    }
    
    @Test
    public void test_setScope() {
        factoryToTest.setScope("scope");
        Mockito.verify(delegate).setScope("scope");
    }
    
    @Test
    public void test_getScope() {
        factoryToTest.getScope();
        Mockito.verify(delegate).getScope();
    }
    
    @Test
    public void test_generateState() {
        factoryToTest.generateState();
        Mockito.verify(delegate).generateState();
    }
    
    @Test
    public void test_supportsStateParameter() {
        factoryToTest.supportsStateParameter();
        Mockito.verify(delegate).supportsStateParameter();
    }
    
    @Test
    public void test_getOAuthOperations() {
        factoryToTest.getOAuthOperations();
        Mockito.verify(delegate).getOAuthOperations();
    }
    
    @Test
    public void test_createConnection() {
        AccessGrant grant = new AccessGrant("token");
        factoryToTest.createConnection(grant);
        Mockito.verify(delegate).createConnection(grant);
    }
    
    @Test
    public void test_createConnection_data() {
        ConnectionData data = new ConnectionData("providerId", "providerUserId", "displayName", "profileUrl", "imageUrl", "accesstoken", "secret", "refershToken", 1L);
        factoryToTest.createConnection(data);
        Mockito.verify(delegate).createConnection(data);
    }
    
    @Test
    public void test_getProviderId() {
        factoryToTest.getProviderId();
        Mockito.verify(delegate).getProviderId();
    }
}
