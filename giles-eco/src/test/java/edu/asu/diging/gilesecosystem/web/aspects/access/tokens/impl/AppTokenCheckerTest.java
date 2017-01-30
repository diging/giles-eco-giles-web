package edu.asu.diging.gilesecosystem.web.aspects.access.tokens.impl;

import io.jsonwebtoken.MalformedJwtException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.web.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.apps.impl.RegisteredApp;
import edu.asu.diging.gilesecosystem.web.aspects.access.openid.google.CheckerResult;
import edu.asu.diging.gilesecosystem.web.aspects.access.openid.google.ValidationResult;
import edu.asu.diging.gilesecosystem.web.exceptions.InvalidTokenException;
import edu.asu.diging.gilesecosystem.web.service.apps.IRegisteredAppManager;
import edu.asu.diging.gilesecosystem.web.tokens.IAppToken;
import edu.asu.diging.gilesecosystem.web.tokens.ITokenService;
import edu.asu.diging.gilesecosystem.web.tokens.impl.AppToken;

public class AppTokenCheckerTest {

    @Mock
    private ITokenService tokenService;
    
    @Mock
    private IRegisteredAppManager appManager;
    
    @InjectMocks
    private AppTokenChecker checkerToTest;
    
    private final String APP_ID = "app_id";
    private final String REVOKED_APP_ID = "revoked_app_id";
    
    private final String TOKEN_ID = "token_id";
    
    private final String VALID_TOKEN = "valid_token";
    private final String MALFORMED_TOKEN = "malformed_token";
    private final String ILLEGAL_ARGUMENT_TOKEN = "illegal_token";
    private final String INVALID_TOKEN = "invalid_token";
    private final String REVOKED_TOKEN = "revoked_token";
    
    @Before
    public void setUp() {
        checkerToTest = new AppTokenChecker();
        MockitoAnnotations.initMocks(this);
        
        IAppToken validAppToken = new AppToken();
        validAppToken.setAppId(APP_ID);
        validAppToken.setId(TOKEN_ID);
        Mockito.when(tokenService.getAppTokenContents(VALID_TOKEN)).thenReturn(validAppToken);
        
        IRegisteredApp app = new RegisteredApp();
        app.setId(APP_ID);
        app.setName("App");
        app.setTokenIds(Arrays.asList(TOKEN_ID));
        Mockito.when(appManager.getApp(APP_ID)).thenReturn(app);
        
        Mockito.when(tokenService.getAppTokenContents(MALFORMED_TOKEN)).thenThrow(new MalformedJwtException("malformed token"));
        Mockito.when(tokenService.getAppTokenContents(ILLEGAL_ARGUMENT_TOKEN)).thenThrow(new IllegalArgumentException());
        Mockito.when(tokenService.getAppTokenContents(INVALID_TOKEN)).thenReturn(null);
        
        IAppToken revokedAppToken = new AppToken();
        revokedAppToken.setAppId(REVOKED_APP_ID);
        revokedAppToken.setId(REVOKED_TOKEN);
        Mockito.when(tokenService.getAppTokenContents(REVOKED_TOKEN)).thenReturn(revokedAppToken);
        Mockito.when(appManager.getApp(REVOKED_APP_ID)).thenReturn(null);
    }
    
    @Test
    public void test_validateToken_valid() throws GeneralSecurityException, IOException, InvalidTokenException {
        CheckerResult result = checkerToTest.validateToken(VALID_TOKEN, APP_ID);
        Assert.assertNotNull(result.getPayload());
        Assert.assertEquals(ValidationResult.VALID, result.getResult());
    }
    
    @Test(expected = InvalidTokenException.class)
    public void test_validateToken_malformedToken() throws GeneralSecurityException, IOException, InvalidTokenException {
        checkerToTest.validateToken(MALFORMED_TOKEN, APP_ID);
    }
    
    @Test(expected = InvalidTokenException.class)
    public void test_validateToken_illegalArgumentToken() throws GeneralSecurityException, IOException, InvalidTokenException {
        checkerToTest.validateToken(ILLEGAL_ARGUMENT_TOKEN, APP_ID);
    }
    
    @Test
    public void test_validateToken_invalid() throws GeneralSecurityException, IOException, InvalidTokenException {
        CheckerResult result = checkerToTest.validateToken(INVALID_TOKEN, APP_ID);
        Assert.assertEquals(ValidationResult.INVALID, result.getResult());
        Assert.assertNull(result.getPayload());
    }
    
    @Test
    public void test_validateToken_revoked() throws GeneralSecurityException, IOException, InvalidTokenException {
        CheckerResult result = checkerToTest.validateToken(REVOKED_TOKEN, APP_ID);
        Assert.assertEquals(ValidationResult.REVOKED, result.getResult());
        Assert.assertNotNull(result.getPayload());
    }
    
    @Test
    public void test_validateToken_revokedId() throws GeneralSecurityException, IOException, InvalidTokenException {
        IRegisteredApp app = new RegisteredApp();
        app.setId(REVOKED_APP_ID);
        app.setName("App");
        app.setTokenIds(Arrays.asList(TOKEN_ID));
        Mockito.when(appManager.getApp(REVOKED_APP_ID)).thenReturn(app);
        
        CheckerResult result = checkerToTest.validateToken(REVOKED_TOKEN, APP_ID);
        Assert.assertEquals(ValidationResult.REVOKED, result.getResult());
        Assert.assertNotNull(result.getPayload());
    }
}
