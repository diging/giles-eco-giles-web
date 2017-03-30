package edu.asu.diging.gilesecosystem.web.tokens;

import edu.asu.diging.gilesecosystem.web.exceptions.ServerMisconfigurationException;

public interface IIntrospectTokenService {

    public abstract IApiTokenContents introspectAccessToken(String token) throws ServerMisconfigurationException;

}
