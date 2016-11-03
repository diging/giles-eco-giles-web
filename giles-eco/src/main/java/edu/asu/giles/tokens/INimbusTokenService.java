package edu.asu.giles.tokens;

import edu.asu.giles.exceptions.AppMisconfigurationException;
import edu.asu.giles.exceptions.IdentityProviderMisconfigurationException;
import edu.asu.giles.exceptions.InvalidTokenException;

public interface INimbusTokenService {

    public abstract IApiTokenContents getOpenIdToken(String token, String appId) throws IdentityProviderMisconfigurationException, InvalidTokenException, AppMisconfigurationException;

}
