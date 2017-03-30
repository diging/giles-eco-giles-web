package edu.asu.diging.gilesecosystem.web.tokens;

import org.springframework.web.client.RestTemplate;

public interface IAccessTokenRestTemplate {

    public abstract RestTemplate getRestTemplate();

    public abstract void setRestTemplate(RestTemplate restTemplate);
}
