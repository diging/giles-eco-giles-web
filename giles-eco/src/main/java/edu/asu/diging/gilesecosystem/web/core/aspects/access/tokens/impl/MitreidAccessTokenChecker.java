package edu.asu.diging.gilesecosystem.web.core.aspects.access.tokens.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.core.aspects.access.openid.google.CheckerResult;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.openid.google.ValidationResult;
import edu.asu.diging.gilesecosystem.web.core.aspects.access.tokens.IChecker;
import edu.asu.diging.gilesecosystem.web.core.exceptions.ServerMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.core.tokens.IApiTokenContents;
import edu.asu.diging.gilesecosystem.web.core.tokens.impl.IntrospectTokenService;

/**
 * Class to validate access token, calls introspect token service.
 * @author snilapwa
 *
 */
@Service
public class MitreidAccessTokenChecker implements IChecker {
    
    public final static String ID = "MITREID_ACCESSTOKEN";
    
    @Autowired
    private IntrospectTokenService tokenService;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public CheckerResult validateToken(String token, String appId) throws GeneralSecurityException,
            IOException, ServerMisconfigurationException {
        CheckerResult result = new CheckerResult();
        result.setResult(ValidationResult.INVALID);
        
        IApiTokenContents accessToken = tokenService.introspectAccessToken(token);

        result.setPayload(accessToken);
        if (accessToken == null) {
            result.setResult(ValidationResult.INVALID);
        } else {
            result.setResult(ValidationResult.VALID);
        }
        
        return result;
    }

}
