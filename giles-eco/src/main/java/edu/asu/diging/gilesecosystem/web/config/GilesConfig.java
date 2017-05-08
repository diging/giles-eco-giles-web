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

import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.septemberutil.service.impl.SystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

/**
 * Class to initialize rest template for MITREid connect server
 * with protected resource access keys and to initialize
 * message handler class to send exception as kafka topic.
 * 
 * @author snilapwa
 *
 */
@Configuration
public class GilesConfig {

    @Autowired
    private IPropertiesManager propertyManager;

    @Bean(name = "accessTokenRestTemplate")
    public RestTemplate getAccessTokenRestTemplate() {
        // use mitreid connect client with protected resource access keys
        final String clientId = propertyManager.getProperty(Properties.MITREID_INTROSPECT_CLIENT_ID);
        final String clientSecret = propertyManager.getProperty(Properties.MITREID_INTROSPECT_SECRET);

        HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory) {
            @Override
            protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
                ClientHttpRequest httpRequest = super.createRequest(url, method);
                httpRequest.getHeaders().add("Authorization",
                        String.format("Basic %s", Base64.encode(String.format("%s:%s", clientId, clientSecret))));
                return httpRequest;
            }
        };

    }
    
    @Bean
    public ISystemMessageHandler getMessageHandler() {
        return new SystemMessageHandler(propertyManager.getProperty(Properties.APPLICATION_ID));
    }
}
