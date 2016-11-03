package edu.asu.giles.aspects.access.tokens.impl;

import io.jsonwebtoken.MalformedJwtException;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.openid.google.ValidationResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.exceptions.AppMisconfigurationException;
import edu.asu.giles.exceptions.IdentityProviderMisconfigurationException;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.exceptions.ServerMisconfigurationException;
import edu.asu.giles.tokens.IApiTokenContents;
import edu.asu.giles.tokens.INimbusTokenService;

@Service
public class MitreidChecker implements IChecker {
    
    public final static String ID = "MITREID";
    
    @Autowired
    private INimbusTokenService tokenService;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CheckerResult validateToken(String token, String appId) throws GeneralSecurityException,
            IOException, InvalidTokenException, ServerMisconfigurationException {
        CheckerResult result = new CheckerResult();
        result.setResult(ValidationResult.INVALID);
        
        IApiTokenContents idToken;
        try {
            idToken = tokenService.getOpenIdToken(token, appId);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("The provided token is not a valid JWT token.", e);
        } catch (IdentityProviderMisconfigurationException e) {
            throw new ServerMisconfigurationException("Identity Provider has not been properly registered.", e);
        } catch (AppMisconfigurationException e) {
            throw new ServerMisconfigurationException("App has not been properly registered.", e);
        }
        
        result.setPayload(idToken);
        if (idToken == null) {
            result.setResult(ValidationResult.INVALID);
        } else if (!idToken.isExpired()) {
            result.setResult(ValidationResult.VALID);
        } else {
            result.setResult(ValidationResult.EXPIRED);
        }
        
        return result;
    }

}
