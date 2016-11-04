package edu.asu.diging.gilesecosystem.web.tokens;

import edu.asu.diging.gilesecosystem.web.exceptions.AppMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.exceptions.IdentityProviderMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.exceptions.InvalidTokenException;

public interface INimbusTokenService {

    public abstract IApiTokenContents getOpenIdToken(String token, String appId) throws IdentityProviderMisconfigurationException, InvalidTokenException, AppMisconfigurationException;

}
