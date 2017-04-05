package edu.asu.diging.gilesecosystem.web.config;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.util.Base64;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

/**
 * Class to initialize rest template for MITREid connect server
 * with protected resource access keys
 * 
 * @author snilapwa
 *
 */
@Configuration
public class AccessTokenRestTemplateConfig {

    @Autowired
    private IPropertiesManager propertyManager;

    private HttpComponentsClientHttpRequestFactory factory;

    private RestTemplate restTemplate;

    public AccessTokenRestTemplateConfig() {
        this(HttpClientBuilder.create().useSystemProperties().build());
    }

    public AccessTokenRestTemplateConfig(HttpClient httpClient) {
        this.factory = new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestTemplate getRestTemplate() {
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
        
        return restTemplate;
    }
}
