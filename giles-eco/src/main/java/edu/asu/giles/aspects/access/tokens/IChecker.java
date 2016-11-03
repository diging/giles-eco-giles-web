package edu.asu.giles.aspects.access.tokens;

import java.io.IOException;
import java.security.GeneralSecurityException;

import edu.asu.giles.aspects.access.openid.google.CheckerResult;
import edu.asu.giles.exceptions.InvalidTokenException;
import edu.asu.giles.exceptions.ServerMisconfigurationException;

public interface IChecker {
    
    public String getId();

    public CheckerResult validateToken(String token, String appId)
            throws GeneralSecurityException, IOException, InvalidTokenException, ServerMisconfigurationException;

}