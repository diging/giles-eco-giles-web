package edu.asu.diging.gilesecosystem.web.config.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.web.config.IAdjustableConnectionFactory;
import edu.asu.diging.gilesecosystem.web.config.IReloadService;
import edu.asu.diging.gilesecosystem.web.config.social.AdjustableGoogleConnectionFactory;
import edu.asu.diging.gilesecosystem.web.exceptions.FactoryDoesNotExistException;

@Service
public class ReloadService implements IReloadService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, IAdjustableConnectionFactory> connectionFactories;
    
    @PostConstruct
    public void init() {
        connectionFactories = new HashMap<String, IAdjustableConnectionFactory>();
    }
    
    @Override
    public void addFactory(String factoryName, IAdjustableConnectionFactory factory) {
        connectionFactories.put(factoryName, factory);
    }
    
    @Override
    public void updateFactory(String factoryName, String clientId, String secret) throws FactoryDoesNotExistException {
        IAdjustableConnectionFactory factory = connectionFactories.get(factoryName);
        if (factory == null) {
            throw new FactoryDoesNotExistException("Factory " + factoryName + " does not exist.");
        }
        factory.update(clientId, secret);
    }
    
    @Override
    public void updateFactory(String factoryName, String clientId, String secret, String serverUrl) throws FactoryDoesNotExistException {
        IAdjustableConnectionFactory factory = connectionFactories.get(factoryName);
        if (factory == null) {
            throw new FactoryDoesNotExistException("Factory " + factoryName + " does not exist.");
        }
        factory.setProviderUrl(serverUrl);
        factory.update(clientId, secret);
    }
}
