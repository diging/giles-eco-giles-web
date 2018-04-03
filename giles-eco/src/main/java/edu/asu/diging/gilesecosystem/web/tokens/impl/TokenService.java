package edu.asu.diging.gilesecosystem.web.tokens.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.apps.IRegisteredApp;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.tokens.IApiTokenContents;
import edu.asu.diging.gilesecosystem.web.tokens.IAppToken;
import edu.asu.diging.gilesecosystem.web.tokens.ITokenService;
import edu.asu.diging.gilesecosystem.web.users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

/**
 * Class to create new user tokens for access to the REST api.
 * 
 * @author Julia Damerow
 *
 */
@Service
public class TokenService implements ITokenService {
    
    /**
     * 4 hours
     */
    private int timeTillExpiration = 14400000;
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private ISystemMessageHandler messageHandler;

    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenService#generateToken(java.lang.String)
     */
    @Override
    public String generateApiToken(User user) {
        String compactJws = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date((new Date()).getTime() + timeTillExpiration))
                .signWith(SignatureAlgorithm.HS512, propertiesManager.getProperty(Properties.SIGNING_KEY))
                .compact();
        
        return compactJws;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenService#getTokenContents(java.lang.String)
     */
    @Override
    public IApiTokenContents getApiTokenContents(String token) {
        IApiTokenContents contents = new ApiTokenContents();
        contents.setExpired(true);
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(propertiesManager.getProperty(Properties.SIGNING_KEY)).parseClaimsJws(token);
            Claims claims = jws.getBody(); 
            contents.setUsername(claims.getSubject());
            Date expirationTime = claims.getExpiration();
            if (expirationTime != null) {
                contents.setExpired(expirationTime.before(new Date()));
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            messageHandler.handleMessage("Token is expired.", e, MessageType.INFO);
            contents.setExpired(true); 
        } catch (SignatureException e) {
            messageHandler.handleMessage("Token signature not correct.", e, MessageType.WARNING);
            return null;
        } 
        
        return contents;
    }

    @Override
    public IAppToken generateAppToken(IRegisteredApp app) {
        String tokenId = UUID.randomUUID().toString();
        String compactJws = Jwts.builder()
                .setSubject(app.getName())
                .claim("appId", app.getId())
                .claim("tokenId", tokenId)
                .claim("providerId", app.getProviderId())
                .claim("authorizationType", app.getAuthorizationType())
                .signWith(SignatureAlgorithm.HS256, propertiesManager.getProperty(Properties.SIGNING_KEY_APPS))
                .compact();
        
        IAppToken token = new AppToken();
        token.setToken(compactJws);
        token.setId(tokenId);
        token.setAppId(app.getId());
        token.setProviderId(app.getProviderId());
        token.setAuthorizationType(app.getAuthorizationType());
        return token;
    }
    
    @Override
    public IAppToken getAppTokenContents(String token) {
        IAppToken appToken = new AppToken();
        IApiTokenContents contents = new ApiTokenContents();
        contents.setExpired(true);
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(propertiesManager.getProperty(Properties.SIGNING_KEY_APPS)).parseClaimsJws(token);
            Claims claims = jws.getBody(); 
            
            appToken.setAppId(claims.get("appId", String.class));
            appToken.setId(claims.get("tokenId", String.class));
            appToken.setProviderId(claims.get("providerId", String.class));
            appToken.setAuthorizationType(claims.get("authorizationType", String.class));
            contents.setUsername(claims.getSubject());
            Date expirationTime = claims.getExpiration();
            if (expirationTime != null) {
                contents.setExpired(expirationTime.before(new Date()));
            }
        
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.INFO);
            contents.setExpired(true);
        } catch (SignatureException e) {
            messageHandler.handleMessage(e.getMessage(), e, MessageType.ERROR);
            return null;
        } 
        
        return appToken;
    }
    
}
