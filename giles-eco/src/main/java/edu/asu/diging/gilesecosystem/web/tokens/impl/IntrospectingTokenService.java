package edu.asu.diging.gilesecosystem.web.tokens.impl;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
import com.nimbusds.jose.util.Base64;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class IntrospectingTokenService {

    @Autowired
    private IPropertiesManager propertyManager;

    private HttpComponentsClientHttpRequestFactory factory;

    public IntrospectingTokenService() {
        this(HttpClientBuilder.create().useSystemProperties().build());
    }

    public IntrospectingTokenService(HttpClient httpClient) {
        this.factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    boolean isValidAccessToken(String accessToken) {

        final String clientId = propertyManager.getProperty(Properties.MITREID_INTROSPECT_CLIENT_ID);
        final String clientSecret = propertyManager.getProperty(Properties.MITREID_INTROSPECT_SECRET);

        String serverURL = propertyManager.getProperty(Properties.MITREID_SERVER_URL);

        if (serverURL == null || serverURL.isEmpty()) {
            logger.error("Unable to load server URL");
            return false;
        }

        // find out which URL to ask
        String introspectionUrl = propertyManager.getProperty(Properties.MITREID_SERVER_URL) + "/introspect";
        String validatedToken = null;

        RestTemplate restTemplate;
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();

        restTemplate = new RestTemplate(factory) {
            @Override
            protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
                ClientHttpRequest httpRequest = super.createRequest(url, method);
                httpRequest.getHeaders().add("Authorization",
                        String.format("Basic %s", Base64.encode(String.format("%s:%s", clientId, clientSecret))));
                return httpRequest;
            }
        };

        form.add("token", accessToken);
        try {
            validatedToken = restTemplate.postForObject(introspectionUrl, form, String.class);
        } catch (RestClientException rce) {
            logger.error("validateToken", rce);
            return false;
        }

        if (validatedToken != null) {
            // parse the json
            JsonElement jsonRoot = new JsonParser().parse(validatedToken);
            if (!jsonRoot.isJsonObject()) {
                return false; // didn't get a proper JSON object
            }

            JsonObject tokenResponse = jsonRoot.getAsJsonObject();

            if (tokenResponse.get("error") != null) {
                // report an error?
                logger.error("Got an error back: " + tokenResponse.get("error") + ", "
                        + tokenResponse.get("error_description"));
                return false;
            }

            if (!tokenResponse.get("active").getAsBoolean()) {
                // non-valid token
                logger.info("Server returned non-active token");
                return false;
            }

            // validated token is not null and has no error, it is a valid
            // access token
            return true;
        }

        // validated token is null
        return false;
    }

}
