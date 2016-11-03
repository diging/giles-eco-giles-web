package edu.asu.giles.service.properties.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.PathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import edu.asu.giles.exceptions.GilesPropertiesStorageException;
import edu.asu.giles.service.properties.IPropertiesManager;

@PropertySource("classpath:/config.properties")
@Service
public class PropertiesManager implements IPropertiesManager {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Environment env;
    
    private PropertiesPersister persister;
    private Properties properties;
    private PathResource customPropsResource;
    
    @PostConstruct
    public void init() throws IOException, URISyntaxException {
        persister = new DefaultPropertiesPersister();
        properties = new Properties();
        
        URL resURL = getClass().getResource("/custom.properties");
        customPropsResource = new PathResource(resURL.toURI());
        
        persister.load(properties, customPropsResource.getInputStream());      
    }
    
    @Override
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return value.trim();
        }
        
        value = env.getProperty(key);
        if (value != null) {
            value = value.trim();
        }
        return value;
    }
    
    @Override
    public void setProperty(String key, String value) throws GilesPropertiesStorageException {
        properties.setProperty(key, value);
        saveProperties();
    }
    
    @Override
    public void updateProperties(Map<String, String> props) throws GilesPropertiesStorageException {
        for (String key : props.keySet()) {
            properties.setProperty(key, props.get(key));
        }
        saveProperties();
    }
    
    protected void saveProperties() throws GilesPropertiesStorageException {
        try {
            persister.store(properties, customPropsResource.getOutputStream(), "Giles custom properties.");
        } catch (IOException e) {
            throw new GilesPropertiesStorageException("Could not store properties.", e);
        }
    }
}
