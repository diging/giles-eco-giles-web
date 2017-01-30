package edu.asu.diging.gilesecosystem.web.tokens.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.social.google.api.drive.DriveApp.AppIcon;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.apps.impl.RegisteredApp;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.tokens.IApiTokenContents;
import edu.asu.diging.gilesecosystem.web.tokens.IAppToken;
import edu.asu.diging.gilesecosystem.web.users.User;

public class TokenServiceTest {

    @Mock
    private IPropertiesManager propertiesManager;

    @InjectMocks
    private TokenService serviceToTest;

    private final String SIGNING_SECRET = "Wz5HrfevjItOZSzEU6fU1r4E2KDIxRR3RXOmCLdm7rdPn/qAHg1jmTu2wXtrEDDbjxOj82ZAg1LTLQdAlBLiRA==";
    private final String SIGNING_SECRET_APPS = "0pRhQg/QuuyqXysggb+BbScs8fcbNRV2zC2aL73IDLXqOFlHOPgVgbzf2rZBqijGKMLBnOlKOPzs9zpJ9TDi+w==";
    private final String USERNAME = "username";

    @Before
    public void setUp() {
        serviceToTest = new TokenService();
        MockitoAnnotations.initMocks(this);

        Mockito.when(propertiesManager.getProperty(Properties.SIGNING_KEY)).thenReturn(
                SIGNING_SECRET);
        Mockito.when(propertiesManager.getProperty(Properties.SIGNING_KEY_APPS))
                .thenReturn(SIGNING_SECRET_APPS);
    }

    @Test
    public void test_generateApiToken() {
        User user = new User();
        user.setUsername(USERNAME);
        String token = serviceToTest.generateApiToken(user);

        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(propertiesManager.getProperty(Properties.SIGNING_KEY))
                .parseClaimsJws(token);
        Claims claims = jws.getBody();
        Assert.assertEquals(USERNAME, claims.getSubject());
        // assert there is an expiration date and it has not yet passed
        Assert.assertNotNull(claims.getExpiration());
        Assert.assertFalse(claims.getExpiration().before(new Date()));
    }

    @Test
    public void test_getApiTokenContents_valid() {
        String compactJws = Jwts
                .builder()
                .setSubject(USERNAME)
                .setExpiration(new Date((new Date()).getTime() + 14400000))
                .signWith(SignatureAlgorithm.HS512,
                        propertiesManager.getProperty(Properties.SIGNING_KEY)).compact();

        IApiTokenContents tokenContents = serviceToTest.getApiTokenContents(compactJws);
        Assert.assertEquals(USERNAME, tokenContents.getUsername());
        Assert.assertFalse(tokenContents.isExpired());
    }

    @Test
    public void test_getApiTokenContents_expired() throws InterruptedException {
        String compactJws = Jwts
                .builder()
                .setSubject(USERNAME)
                .setExpiration(new Date((new Date()).getTime() + 1))
                .signWith(SignatureAlgorithm.HS512,
                        propertiesManager.getProperty(Properties.SIGNING_KEY)).compact();

        TimeUnit.SECONDS.sleep(1);
        IApiTokenContents tokenContents = serviceToTest.getApiTokenContents(compactJws);
        Assert.assertTrue(tokenContents.isExpired());
    }

    @Test
    public void test_getApiTokenContents_invalid() {
        String wrongSigningSecret = "ar19Lgc6fKZ8LONpoDXE0bcn7gT+UJoy6ti7/PvmcvMg4GZMp5X2nvEkXpfhLzpdGYzULXWh4q/NJUANLr6yAA==";
        String compactJws = Jwts.builder().setSubject(USERNAME)
                .setExpiration(new Date((new Date()).getTime() + 14400000))
                .signWith(SignatureAlgorithm.HS512, wrongSigningSecret).compact();
        IApiTokenContents tokenContents = serviceToTest.getApiTokenContents(compactJws);
        Assert.assertNull(tokenContents);
    }

    @Test
    public void test_generateAppToken() {
        String APP_ID = "app_id";
        String APP_NAME = "app_name";
        String PROVIDER_CLIENT_ID = "provider_client_id";
        String PROVIDER_ID = "provider_id";

        IRegisteredApp app = new RegisteredApp();
        app.setId(APP_ID);
        app.setName(APP_NAME);
        app.setProviderClientId(PROVIDER_CLIENT_ID);
        app.setProviderId(PROVIDER_ID);

        IAppToken token = serviceToTest.generateAppToken(app);
        Assert.assertEquals(APP_ID, token.getAppId());
        Assert.assertEquals(PROVIDER_ID, token.getProviderId());
        Assert.assertNotNull(token.getId());

        Jws<Claims> jws = Jwts
                .parser()
                .setSigningKey(propertiesManager.getProperty(Properties.SIGNING_KEY_APPS))
                .parseClaimsJws(token.getToken());
        Claims claims = jws.getBody();
        Assert.assertEquals(APP_NAME, claims.getSubject());
        Assert.assertEquals(APP_ID, claims.get("appId"));
        Assert.assertEquals(PROVIDER_ID, claims.get("providerId"));
        Assert.assertEquals(token.getId(), claims.get("tokenId"));
    }
    
    @Test
    public void test_getAppTokenContents_valid() {
        String APP_ID = "app_id";
        String APP_NAME = "app_name";
        String PROVIDER_ID = "provider_id";
        String TOKEN_ID = "token_id";
        
        String compactJws = Jwts
                .builder()
                .setSubject(APP_NAME)
                .claim("appId", APP_ID)
                .claim("tokenId", TOKEN_ID)
                .claim("providerId", PROVIDER_ID)
                .signWith(SignatureAlgorithm.HS512,
                        propertiesManager.getProperty(Properties.SIGNING_KEY_APPS)).compact();
        
        IAppToken appToken = serviceToTest.getAppTokenContents(compactJws);
        Assert.assertNotNull(appToken);
        Assert.assertEquals(APP_ID, appToken.getAppId());
        Assert.assertEquals(TOKEN_ID, appToken.getId());
        Assert.assertEquals(PROVIDER_ID, appToken.getProviderId());
    }
    
    @Test
    public void test_getAppTokenContents_invalidSignature() {
        String APP_ID = "app_id";
        String APP_NAME = "app_name";
        String PROVIDER_ID = "provider_id";
        String TOKEN_ID = "token_id";
        
        String wrongSigningSecret = "ar19Lgc6fKZ8LONpoDXE0bcn7gT+UJoy6ti7/PvmcvMg4GZMp5X2nvEkXpfhLzpdGYzULXWh4q/NJUANLr6yAA==";
        String compactJws = Jwts
                .builder()
                .setSubject(APP_NAME)
                .claim("appId", APP_ID)
                .claim("tokenId", TOKEN_ID)
                .claim("providerId", PROVIDER_ID)
                .signWith(SignatureAlgorithm.HS512,
                        wrongSigningSecret).compact();
        
        IAppToken token = serviceToTest.getAppTokenContents(compactJws);
        Assert.assertNull(token);
    }
    
    @Test
    public void test_getAppTokenContents_expired() throws InterruptedException {
        String APP_ID = "app_id";
        String APP_NAME = "app_name";
        String PROVIDER_ID = "provider_id";
        String TOKEN_ID = "token_id";
        
        String compactJws = Jwts
                .builder()
                .setSubject(APP_NAME)
                .setExpiration(new Date((new Date()).getTime() + 1))
                .claim("appId", APP_ID)
                .claim("tokenId", TOKEN_ID)
                .claim("providerId", PROVIDER_ID)
                .signWith(SignatureAlgorithm.HS512,
                        propertiesManager.getProperty(Properties.SIGNING_KEY_APPS)).compact();
        
        TimeUnit.SECONDS.sleep(1);
        IAppToken appToken = serviceToTest.getAppTokenContents(compactJws);
        Assert.assertNull(appToken);
    }
}
