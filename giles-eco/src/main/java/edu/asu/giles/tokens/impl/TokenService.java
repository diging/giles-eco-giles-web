package edu.asu.giles.tokens.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.tokens.IApiTokenContents;
import edu.asu.giles.tokens.IAppToken;
import edu.asu.giles.tokens.ITokenService;
import edu.asu.giles.users.User;

/**
 * Class to create new user tokens for access to the REST api.
 * 
 * @author Julia Damerow
 *
 */
@Service
public class TokenService implements ITokenService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 4 hours
     */
    private int timeTillExpiration = 14400000;
    
    @Autowired
    private IPropertiesManager propertiesManager;

    /* (non-Javadoc)
     * @see edu.asu.giles.tokens.impl.ITokenService#generateToken(java.lang.String)
     */
    @Override
    public String generateApiToken(User user) {
        String compactJws = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date((new Date()).getTime() + timeTillExpiration))
                .signWith(SignatureAlgorithm.HS512, propertiesManager.getProperty(IPropertiesManager.SIGNING_KEY))
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
            Jws<Claims> jws = Jwts.parser().setSigningKey(propertiesManager.getProperty(IPropertiesManager.SIGNING_KEY)).parseClaimsJws(token);
            Claims claims = jws.getBody(); 
            contents.setUsername(claims.getSubject());
            Date expirationTime = claims.getExpiration();
            if (expirationTime != null) {
                contents.setExpired(expirationTime.before(new Date()));
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            logger.info("Token is expired.", e);
            contents.setExpired(true); 
        } catch (SignatureException e) {
            logger.warn("Token signature not correct.", e);
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
                .signWith(SignatureAlgorithm.HS256, propertiesManager.getProperty(IPropertiesManager.SIGNING_KEY_APPS))
                .compact();
        
        IAppToken token = new AppToken();
        token.setToken(compactJws);
        token.setId(tokenId);
        token.setAppId(app.getId());
        token.setProviderId(app.getProviderId());
        return token;
    }
    
    @Override
    public IAppToken getAppTokenContents(String token) {
        IAppToken appToken = new AppToken();
        
        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(propertiesManager.getProperty(IPropertiesManager.SIGNING_KEY_APPS)).parseClaimsJws(token);
            Claims claims = jws.getBody(); 
            
            appToken.setAppId(claims.get("appId", String.class));
            appToken.setId(claims.get("tokenId", String.class));
            appToken.setProviderId(claims.get("providerId", String.class));
        
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return null;
        } catch (SignatureException e) {
            return null;
        } 
        
        return appToken;
    }
    
}
