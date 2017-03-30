package edu.asu.diging.gilesecosystem.web.tokens.impl;

import java.io.IOException;
import java.net.URI;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.util.Base64;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.tokens.IAccessTokenRestTemplate;

@Service
public class AccessTokenRestTemplate extends RestTemplate implements IAccessTokenRestTemplate {

    @Autowired
    private IPropertiesManager propertyManager;

    private HttpComponentsClientHttpRequestFactory factory;

    private RestTemplate restTemplate;

    public AccessTokenRestTemplate() {
        this(HttpClientBuilder.create().useSystemProperties().build());
    }

    public AccessTokenRestTemplate(HttpClient httpClient) {
        this.factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @PostConstruct
    public void init() {
        // use mitreid connect client with protected resource access keys
        final String clientId = propertyManager.getProperty(Properties.MITREID_INTROSPECT_CLIENT_ID);
        final String clientSecret = propertyManager.getProperty(Properties.MITREID_INTROSPECT_SECRET);

        restTemplate = new RestTemplate(factory) {
            @Override
            protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
                ClientHttpRequest httpRequest = super.createRequest(url, method);
                httpRequest.getHeaders().add("Authorization",
                        String.format("Basic %s", Base64.encode(String.format("%s:%s", clientId, clientSecret))));
                return httpRequest;
            }
        };

    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
