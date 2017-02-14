package edu.asu.diging.gilesecosystem.web.service.search.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.exceptions.NoFreddieInstanceConfigured;
import edu.asu.diging.gilesecosystem.web.exceptions.SearchException;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.service.search.ISearchConnector;

@Service
public class FreddieConnector implements ISearchConnector {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final static String SEARCH_PATH = "/rest/search/";

    @Autowired
    private IPropertiesManager propertiesManager;
    
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.search.impl.ISearchService#search(java.lang.String, java.lang.String)
     */
    @Override
    public List<SearchResult> search(String username, String searchTerms) throws NoFreddieInstanceConfigured, SearchException {
        String freddieHost = propertiesManager.getProperty(Properties.FREDDIE_HOST);
        if (freddieHost == null || freddieHost.isEmpty()) {
            throw new NoFreddieInstanceConfigured();
        }
        
        String url = freddieHost + SEARCH_PATH + username + "?q=" + searchTerms;
        logger.debug("Contacting: " + url);
        SearchResult[] results;
        RestTemplate restTemplate = new RestTemplate();
        try {
            results = restTemplate.getForObject(new URI(url), SearchResult[].class);
        } catch (RestClientException | URISyntaxException e) {
            throw new SearchException(e);
        }
        
        return Arrays.asList(results);
    }
}
