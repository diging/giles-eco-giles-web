package edu.asu.diging.gilesecosystem.web.tokens.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.config.GilesTokenConfig;
import edu.asu.diging.gilesecosystem.web.exceptions.AppMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.exceptions.ServerMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.tokens.IApiTokenContents;

public class IntrospectTokenServiceTest {

    @Mock
    private IPropertiesManager propertiesManager;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private IntrospectTokenService serviceToTest;

    private final String MITREID_INTROSPECT_URL = "introspection_url";
    private final String VALID_ACCESS_TOKEN = "valid_accessToken";
    private final String INVALID_ACCESS_TOKEN = "invalid_accessToken";
    private final String VALID_REST_RESPONSE = "{\"active\":true,\"exp\":1490812640,\"sub\":\"01921.FLANRJQW\"}";
    private final String INVALID_REST_RESPONSE = "{\"active\":false}";

    @Before
    public void setUp() {
        serviceToTest = new IntrospectTokenService();
        MockitoAnnotations.initMocks(this);

        Mockito.when(propertiesManager.getProperty(Properties.MITREID_INTROSPECT_URL))
                .thenReturn(MITREID_INTROSPECT_URL);
    }

    @Test
    public void test_introspectAccessToken_valid() throws AppMisconfigurationException, ServerMisconfigurationException {
        Mockito.when(restTemplate.postForObject(Matchers.eq(MITREID_INTROSPECT_URL), Matchers.anyMap(),
                Matchers.anyObject())).thenReturn(VALID_REST_RESPONSE);
        IApiTokenContents token = serviceToTest.introspectAccessToken(VALID_ACCESS_TOKEN);
        Assert.assertNotNull(token);
        Assert.assertNotNull(token.getUsername());
        Assert.assertNotNull(token.isExpired());
    }

    @Test
    public void test_introspectAccessToken_invalid() throws AppMisconfigurationException, ServerMisconfigurationException {
        Mockito.when(restTemplate.postForObject(Matchers.eq(MITREID_INTROSPECT_URL), Matchers.anyMap(),
                Matchers.anyObject())).thenReturn(INVALID_REST_RESPONSE);
        IApiTokenContents token = serviceToTest.introspectAccessToken(INVALID_ACCESS_TOKEN);
        Assert.assertNull(token);
    }

    @Test(expected = ServerMisconfigurationException.class)
    public void test_introspectAccessToken_misconfigured_server()
            throws AppMisconfigurationException, ServerMisconfigurationException {
        Mockito.when(propertiesManager.getProperty(Properties.MITREID_INTROSPECT_URL)).thenReturn(null);
        serviceToTest.introspectAccessToken(VALID_ACCESS_TOKEN);
    }

}
