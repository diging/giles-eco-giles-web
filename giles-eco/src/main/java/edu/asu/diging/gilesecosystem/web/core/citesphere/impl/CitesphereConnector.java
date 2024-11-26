package edu.asu.diging.gilesecosystem.web.core.citesphere.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.citesphere.ICitesphereConnector;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class CitesphereConnector implements ICitesphereConnector {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPropertiesManager propertiesManager;

    private RestTemplate restTemplate;
    
    private String currentAccessToken;
    
    public CitesphereConnector() {
        restTemplate = new RestTemplate();
    }
  
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.citesphere.impl.ICitesphereConnector#checkUserAccess(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasAccess(String documentId, String username) {
        String citesphereTokenEndpoint = propertiesManager
                .getProperty(Properties.CITESPHERE_CHECK_ACCESS_ENDPOINT).replace("{0}", documentId);
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", username);
        
        try {
            // we get a 200 if user has access, so unless there is an exception all is good
            sendRequest(citesphereTokenEndpoint, parameters, String.class);
            return true;
        } catch (HttpClientErrorException e) {
            logger.warn("Access denied.", e);
            return false;
        }
    }
    
    @Override
    public boolean hasAccessViaProgressId(String progressId, String username) {
        String citesphereTokenEndpoint = propertiesManager
                .getProperty(Properties.CITESPHERE_CHECK_ACCESS_ENDPOINT_PROGRESS_ID).replace("{0}", progressId);
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("username", username);
        
        try {
            // we get a 200 if user has access, so unless there is an exception all is good
            sendRequest(citesphereTokenEndpoint, parameters, String.class);
            return true;
        } catch (HttpClientErrorException e) {
            logger.warn("Access denied.", e);
            return false;
        }
    }
    
    private String getAccessToken() {
        String clientId = propertiesManager.getProperty(Properties.CITESPHERE_CLIENT_ID);
        String secret = propertiesManager
                .getProperty(Properties.CITESPHERE_CLIENT_SECRET);
        String citesphereBase = propertiesManager
                .getProperty(Properties.CITESPHERE_BASE_URL);
        String citesphereTokenEndpoint = propertiesManager
                .getProperty(Properties.CITESPHERE_TOKEN_ENDPOINT);
        String citesphereScopes = propertiesManager
                .getProperty(Properties.CITESPHERE_SCOPES);

        AuthorizationGrant clientGrant = new ClientCredentialsGrant();
        ClientID clientID = new ClientID(clientId);
        Secret clientSecret = new Secret(secret);
        ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
        Scope scope = new Scope(citesphereScopes.split(","));

        URI tokenEndpoint;
        try {
            tokenEndpoint = new URI(citesphereBase + citesphereTokenEndpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }

        TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, clientGrant,
                scope);
        TokenResponse response;
        try {
            response = TokenResponse.parse(request.toHTTPRequest().send());
        } catch (ParseException | IOException e) {
            throw new IllegalArgumentException(e);
        }

        if (!response.indicatesSuccess()) {
            logger.error(
                    "Giles could not retrieve access token. Maybe the configuration is wrong.");
            throw new IllegalArgumentException(
                    "Giles could not retrieve access token. Maybe the configuration is wrong.");
        }

        AccessToken accessTokenResponse = ((AccessTokenResponse)response).getTokens().getAccessToken();
        return accessTokenResponse.getValue();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.core.citesphere.impl.ICitesphereConnector#sendRequest(java.lang.String, java.util.Map, java.lang.Class)
     */
    @Override
    public <T> T sendRequest(String endpoint, Map<String, String> parameters, Class<T> responseType) throws HttpClientErrorException {
        if (currentAccessToken == null) {
            currentAccessToken = getAccessToken();
        }
        
        String citesphereBase = propertiesManager
                .getProperty(Properties.CITESPHERE_BASE_URL);
         
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + currentAccessToken);
        
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        List<String> parameterEntries = parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.toList());
        String parameterString = String.join("&", parameterEntries);
        String citesphereUrl = citesphereBase + endpoint + "?" + parameterString; 
        
        try {
            return restTemplate.postForObject(citesphereUrl, entity, responseType, new Object[] {});
        } catch(HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                currentAccessToken = getAccessToken();
                headers.set("Authorization", "Bearer " + currentAccessToken);
                return restTemplate.postForObject(citesphereUrl, entity, responseType, new Object[] {});
            }
            throw ex;
        }
    }
}
