package edu.asu.diging.gilesecosystem.web.tokens.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.tokens.IApiTokenContents;
import edu.asu.diging.gilesecosystem.web.users.User;

public class TokenServiceTest {

    @Mock
    private IPropertiesManager propertiesManager;
    
    @InjectMocks
    private TokenService serviceToTest;
    
    private final String SIGING_SECRET = "Wz5HrfevjItOZSzEU6fU1r4E2KDIxRR3RXOmCLdm7rdPn/qAHg1jmTu2wXtrEDDbjxOj82ZAg1LTLQdAlBLiRA==";
    private final String USERNAME = "username";
    
    @Before
    public void setUp() {
        serviceToTest = new TokenService();
        MockitoAnnotations.initMocks(this);
        
        Mockito.when(propertiesManager.getProperty(Properties.SIGNING_KEY)).thenReturn(SIGING_SECRET);
        
    }
    
    @Test
    public void test_generateApiToken() {
        User user = new User();
        user.setUsername(USERNAME);
        String token = serviceToTest.generateApiToken(user);
        
        Jws<Claims> jws = Jwts.parser().setSigningKey(propertiesManager.getProperty(Properties.SIGNING_KEY)).parseClaimsJws(token);
        Claims claims = jws.getBody(); 
        Assert.assertEquals(USERNAME, claims.getSubject());
        // assert there is an expiration date and it has not yet passed
        Assert.assertNotNull(claims.getExpiration());
        Assert.assertFalse(claims.getExpiration().before(new Date()));
    }
    
    @Test
    public void test_getApiTokenContents_valid() {
        String compactJws = Jwts.builder()
                .setSubject(USERNAME)
                .setExpiration(new Date((new Date()).getTime() + 14400000))
                .signWith(SignatureAlgorithm.HS512, propertiesManager.getProperty(Properties.SIGNING_KEY))
                .compact();
        
        IApiTokenContents tokenContents = serviceToTest.getApiTokenContents(compactJws);
        Assert.assertEquals(USERNAME, tokenContents.getUsername());
        Assert.assertFalse(tokenContents.isExpired());
    }
}
