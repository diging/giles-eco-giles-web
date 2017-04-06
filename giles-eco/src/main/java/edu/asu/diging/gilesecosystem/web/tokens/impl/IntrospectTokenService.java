package edu.asu.diging.gilesecosystem.web.tokens.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.exceptions.ServerMisconfigurationException;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.tokens.IApiTokenContents;
import edu.asu.diging.gilesecosystem.web.tokens.IIntrospectTokenService;

/**
 * Class to introspect access token for MITREid connect server
 * 
 * @author snilapwa
 *
 */
@Service
public class IntrospectTokenService implements IIntrospectTokenService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPropertiesManager propertyManager;

    @Autowired
    private RestTemplate accessTokenRestTemplate;

    /**
     * Calls MITREid connect server introspect api using client with protected
     * resource access keys.
     * 
     * @param accessToken accessToken to pass to introspect Api
     * @return tokenContents with username and expiration status
     * @throws ServerMisconfigurationException
     */
    public IApiTokenContents introspectAccessToken(String accessToken) throws ServerMisconfigurationException {

        String introspectionUrl = propertyManager.getProperty(Properties.MITREID_INTROSPECT_URL);

        if (introspectionUrl == null || introspectionUrl.isEmpty()) {
            throw new ServerMisconfigurationException("Unable to load server URL");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("token", accessToken);
        String validatedToken = null;
        try {
            validatedToken = accessTokenRestTemplate.postForObject(introspectionUrl, form, String.class);
        } catch (RestClientException rce) {
            logger.error("introspectToken", rce);
            return null;
        }

        return parseValidatedToken(validatedToken);
    }

    private IApiTokenContents parseValidatedToken(String validatedToken) {

        if (validatedToken == null) {
            return null;
        }

        // parse the json
        JsonElement jsonRoot = new JsonParser().parse(validatedToken);
        if (!jsonRoot.isJsonObject()) {
            return null; // didn't get a proper JSON object
        }

        JsonObject tokenResponse = jsonRoot.getAsJsonObject();

        if (tokenResponse.get("error") != null) {
            logger.error("Got an error back: " + tokenResponse.get("error") + ", " + tokenResponse.get("error_description"));
            return null;
        }

        if (!tokenResponse.get("active").getAsBoolean()) {
            // non-valid token
            logger.info("Server returned non-active token");
            return null;
        }

        // validated token is not null and has no error, it is a valid
        // access token
        return getTokenContents(tokenResponse);
    }

    private IApiTokenContents getTokenContents(JsonObject tokenResponse) {

        IApiTokenContents tokenContents = new ApiTokenContents();

        // get username, remove leading and trailing "" from username
        JsonElement username = tokenResponse.get("sub");
        if (username != null) {
            tokenContents.setUsername(username.toString().replaceAll("^\"|\"$", ""));
        }
        JsonElement expiryTime = tokenResponse.get("exp");
        if (expiryTime != null) {
            Date expirationTime = new Date(Long.parseLong(expiryTime.toString()) * 1000);
            tokenContents.setExpired(expirationTime.before(new Date()));
        }

        return tokenContents;
    }

}
