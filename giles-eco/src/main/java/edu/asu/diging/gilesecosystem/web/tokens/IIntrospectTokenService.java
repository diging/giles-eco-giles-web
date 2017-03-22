package edu.asu.diging.gilesecosystem.web.tokens;

import edu.asu.diging.gilesecosystem.web.exceptions.AppMisconfigurationException;

public interface IIntrospectTokenService {

    public abstract IApiTokenContents getOpenAccessToken(String token, String appId) throws AppMisconfigurationException;

}
