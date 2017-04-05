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
     * map providerId to provider name
     * @param providerId combination of 'provider' + "_" + 'authorizationType'
     * e.g mitreidconnect_accessToken has provider 'mitreidconnect' with authorization using 'accessToken'
     * make sure not to have '_' in your provider name.
     * providerId does not necessarily have to contain 'authorizationType', in that case providerId just contain
     * 'provider' without '_'
     */
    @Override
    public void addProvider(String providerId) {
        String providerName = env.getProperty(providerId);
        if (providerName == null || providerName.trim().isEmpty()) {
            providerName = WordUtils.capitalize(providerId);
        }
        providers.put(providerId, providerName);
    }
    
    @Override
    public void addProviderTokenChecker(String providerId, String checkerId) {
        providerTokenChecker.put(providerId, checkerId);
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
        if (authorizationType != null && !authorizationType.isEmpty()) {
            return providerTokenChecker.get(providerId + "_" + authorizationType);
        } else {
            return providerTokenChecker.get(providerId);
        }

    }
}
