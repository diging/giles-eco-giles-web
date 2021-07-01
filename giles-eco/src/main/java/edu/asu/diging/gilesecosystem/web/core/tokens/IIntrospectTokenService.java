package edu.asu.diging.gilesecosystem.web.core.tokens;

import edu.asu.diging.gilesecosystem.web.core.exceptions.ServerMisconfigurationException;

/**
 * 
 * interface to implement IntrospectTokenService class to introspect
 * access token with MITREid connect server.
 * @author snilapwa
 */
public interface IIntrospectTokenService {

    /**
     * Calls MITREid connect server introspect api using client with protected
     * resource access keys.
     * 
     * @param accessToken accessToken to pass to introspect Api
     * @return tokenContents with username and expiration status
     * @throws ServerMisconfigurationException
     */
    public abstract IApiTokenContents introspectAccessToken(String token) throws ServerMisconfigurationException;

}
