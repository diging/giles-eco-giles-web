package edu.asu.diging.gilesecosystem.web.core.tokens;

import edu.asu.diging.gilesecosystem.web.core.exceptions.AppMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.IdentityProviderMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.InvalidTokenException;

public interface INimbusTokenService {

    public abstract IApiTokenContents getOpenIdToken(String token, String appId) throws IdentityProviderMisconfigurationException, InvalidTokenException, AppMisconfigurationException;

}
