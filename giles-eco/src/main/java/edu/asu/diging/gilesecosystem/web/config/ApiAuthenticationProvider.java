package edu.asu.diging.gilesecosystem.web.config;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import edu.asu.diging.gilesecosystem.web.config.impl.TokenInfo;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

public class ApiAuthenticationProvider implements AuthenticationProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPropertiesManager propertiesManager;

    private RestTemplate restTemplate;

    public ApiAuthenticationProvider() {
        restTemplate = new RestTemplate();
    }

    @Override
    public Authentication authenticate(Authentication auth)
            throws AuthenticationException {

        String token = auth.getCredentials().toString();

        String accessToken = getAccessToken();
        
        String citesphereBase = propertiesManager
                .getProperty(Properties.CITESPHERE_BASE_URL);
        String citesphereCheckTokenEndpoint = propertiesManager
                .getProperty(Properties.CITESPHERE_CHECK_TOKEN_ENDPOINT);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        String checkTokenUrl = citesphereBase + citesphereCheckTokenEndpoint + "?token=" + token; 
        TokenInfo response;
        try {
            response = restTemplate.postForObject(checkTokenUrl, entity, TokenInfo.class, new Object[] {});
        } catch(HttpClientErrorException ex) {
            throw new BadCredentialsException("Token is invalid for app.", ex);
        }
        
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : response.getAuthorities()) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return new CitesphereToken(authorities);
    }

    public String getAccessToken() {
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

    @Override
    public boolean supports(Class<?> arg0) {
        return arg0.isAssignableFrom(CitesphereToken.class);
    }

}
