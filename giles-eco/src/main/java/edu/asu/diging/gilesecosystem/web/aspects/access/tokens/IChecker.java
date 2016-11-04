package edu.asu.diging.gilesecosystem.web.aspects.access.tokens;

import java.io.IOException;
import java.security.GeneralSecurityException;

import edu.asu.diging.gilesecosystem.web.aspects.access.openid.google.CheckerResult;
import edu.asu.diging.gilesecosystem.web.exceptions.InvalidTokenException;
import edu.asu.diging.gilesecosystem.web.exceptions.ServerMisconfigurationException;

public interface IChecker {
    
    public String getId();

    public CheckerResult validateToken(String token, String appId)
            throws GeneralSecurityException, IOException, InvalidTokenException, ServerMisconfigurationException;

}