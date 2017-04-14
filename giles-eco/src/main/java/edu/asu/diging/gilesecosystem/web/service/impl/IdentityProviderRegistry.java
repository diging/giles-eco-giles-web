package edu.asu.diging.gilesecosystem.web.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.service.IIdentityProviderRegistry;


@Service
@PropertySource("classpath:/providers.properties")
public class IdentityProviderRegistry implements IIdentityProviderRegistry {

    private Map<String, String> providers;
    private Map<String, String> providerTokenChecker;
    
    @Autowired
    private Environment env;
    
    @PostConstruct
    public void init() {
        providers = new HashMap<String, String>();
        providerTokenChecker = new HashMap<>();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IIdentityProviderRegistry#addProvider(java.lang.String)
     */
    /**
     * register a new provider with a given authorization type
     * e.g authorization type 'mitreidconnect_accessToken' has providerId 'mitreidconnect' 
     * with authorization using 'accessToken'. Make sure not to have '_' in your provider name.
     * @param providerId type of provider used for authentication
     * @param authorizationType type of token used, can be null.
     */
    @Override
    public void addProvider(String providerId, String authorizationType) {
        providerId = getProviderIdwithAuthType(providerId, authorizationType);
        String providerName = env.getProperty(providerId);
        if (providerName == null || providerName.trim().isEmpty()) {
            providerName = WordUtils.capitalize(providerId);
        }
        providers.put(providerId, providerName);
    }
    
    @Override
    public void addProviderTokenChecker(String providerId, String authorizationType, String checkerId) {
        providerTokenChecker.put(getProviderIdwithAuthType(providerId, authorizationType), checkerId);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.giles.service.impl.IIdentityProviderRegistry#getProviders()
     */
    @Override
    public Map<String, String> getProviders() {
        return Collections.unmodifiableMap(providers);
    }
    
    @Override
    public String getProviderName(String id) {
        return providers.get(id);
    }
    
    @Override
    public String getCheckerId(String providerId, String authorizationType) {
        return providerTokenChecker.get(getProviderIdwithAuthType(providerId, authorizationType));
    }

    private String getProviderIdwithAuthType(String providerId, String authorizationType) {
        if (authorizationType != null && !authorizationType.trim().isEmpty()) {
            return providerId + "_" + authorizationType;
        }
        return providerId;
    }
}
