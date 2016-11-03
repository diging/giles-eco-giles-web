package edu.asu.giles.aspects.access.tokens.impl;

import io.jsonwebtoken.MalformedJwtException;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.aspects.access.openid.google.ValidationResult;
import edu.asu.giles.aspects.access.tokens.IChecker;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.service.apps.IRegisteredAppManager;
import edu.asu.giles.tokens.IAppToken;
import edu.asu.giles.tokens.ITokenService;

@Service
public class AppTokenChecker implements IChecker {

    public final static String ID = "APP_TOKEN_GILES";

    @Autowired
    private ITokenService tokenService;
    
    @Autowired
    private IRegisteredAppManager appManager;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CheckerResult validateToken(String token, String appId) throws GeneralSecurityException,
            IOException, InvalidTokenException {
        IAppToken appToken;
        
        try {
            appToken = tokenService.getAppTokenContents(token);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("The provided token is not a valid JWT token.", e);
        }
        
        CheckerResult result = new CheckerResult();
        result.setPayload(appToken);
        
        if (appToken == null) {
            result.setResult(ValidationResult.INVALID);
            return result;
        }
        
        IRegisteredApp registeredApp = appManager.getApp(appToken.getAppId());
        if (registeredApp == null) {
            result.setResult(ValidationResult.REVOKED);
            return result;
        }
        
        // token is not revoked
        if (registeredApp.getTokenIds().contains(appToken.getId())) {
            result.setResult(ValidationResult.VALID);
            return result;
        } 
        
        result.setResult(ValidationResult.REVOKED);
        return result;
    }

}
