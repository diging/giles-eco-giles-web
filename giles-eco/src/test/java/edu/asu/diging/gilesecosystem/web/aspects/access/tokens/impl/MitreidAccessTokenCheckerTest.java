package edu.asu.diging.gilesecosystem.web.aspects.access.tokens.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.web.core.aspects.access.openid.google.CheckerResult;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.openid.google.ValidationResult;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.tokens.impl.MitreidAccessTokenChecker;
import edu.asu.diging.gilesecosystem.web.core.exceptions.AppMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.ServerMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.core.tokens.IApiTokenContents;
import edu.asu.diging.gilesecosystem.web.core.tokens.impl.ApiTokenContents;
import edu.asu.diging.gilesecosystem.web.core.tokens.impl.IntrospectTokenService;

public class MitreidAccessTokenCheckerTest {

    @Mock
    private IntrospectTokenService tokenService;

    @InjectMocks
    private MitreidAccessTokenChecker checkerToTest;

    private final String USER_NAME = "user_name";
    private final String APP_ID = "app_id";
    private final String VALID_ACCESS_TOKEN = "valid_access_token";
    private final String INVALID_ACCESS_TOKEN = "invalid_access_token";
    private final String MISCONFIGURED_SERVER_ACCESS_TOKEN = "misconfigured_server_access_token";

    @Before
    public void setUp() throws AppMisconfigurationException, ServerMisconfigurationException {
        checkerToTest = new MitreidAccessTokenChecker();
        MockitoAnnotations.initMocks(this);

        IApiTokenContents validAccessToken = new ApiTokenContents();
        validAccessToken.setUsername(USER_NAME);
        validAccessToken.setExpired(false);
        Mockito.when(tokenService.introspectAccessToken(VALID_ACCESS_TOKEN)).thenReturn(validAccessToken);

        Mockito.when(tokenService.introspectAccessToken(INVALID_ACCESS_TOKEN)).thenReturn(null);
        Mockito.when(tokenService.introspectAccessToken(MISCONFIGURED_SERVER_ACCESS_TOKEN))
                .thenThrow(new ServerMisconfigurationException("Server has not been properly registered"));
    }

    @Test
    public void test_validateToken_valid()
            throws GeneralSecurityException, IOException, ServerMisconfigurationException {
        CheckerResult result = checkerToTest.validateToken(VALID_ACCESS_TOKEN, APP_ID);
        Assert.assertNotNull(result.getPayload());
        Assert.assertEquals(ValidationResult.VALID, result.getResult());
    }

    @Test(expected = ServerMisconfigurationException.class)
    public void test_validateToken_misconfigured_server()
            throws GeneralSecurityException, IOException, ServerMisconfigurationException {
        checkerToTest.validateToken(MISCONFIGURED_SERVER_ACCESS_TOKEN, APP_ID);
    }

    @Test
    public void test_validateToken_invalid()
            throws GeneralSecurityException, IOException, ServerMisconfigurationException {
        CheckerResult result = checkerToTest.validateToken(INVALID_ACCESS_TOKEN, APP_ID);
        Assert.assertNull(result.getPayload());
        Assert.assertEquals(ValidationResult.INVALID, result.getResult());
    }

}
