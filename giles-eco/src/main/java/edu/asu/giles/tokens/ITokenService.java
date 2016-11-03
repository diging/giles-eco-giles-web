package edu.asu.giles.tokens;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import edu.asu.giles.apps.IRegisteredApp;
import edu.asu.giles.service.properties.IPropertiesManager;
import edu.asu.giles.users.User;


public interface ITokenService {

    /**
     * Generate a new user token.
     * 
     * @param username
     * @return
     */
    public abstract String generateApiToken(User user);

    /**
     * Method to get the contents of a token. This method will simply extract the contents
     * and always return a {@link IApiTokenContents} object, even if the token is expired. Classes
     * using this method have to make sure a given token is not expired by calling the 
     * method <code>isExpired</code> of the returned {@link IApiTokenContents} object.
     * 
     * @param token The token to extract the contents from.
     * @return
     */
    public abstract IApiTokenContents getApiTokenContents(String token);

    public abstract IAppToken generateAppToken(IRegisteredApp app);

    public abstract IAppToken getAppTokenContents(String token);

}