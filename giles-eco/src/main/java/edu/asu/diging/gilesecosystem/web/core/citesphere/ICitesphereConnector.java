package edu.asu.diging.gilesecosystem.web.core.citesphere;

import java.util.Map;

import org.springframework.web.client.HttpClientErrorException;

public interface ICitesphereConnector {

    boolean hasAccess(String documentId, String username);

    <T> T sendRequest(String endpoint, Map<String, String> parameters,
            Class<T> responseType) throws HttpClientErrorException;

    boolean hasAccessViaProgressId(String progressId, String username);

}