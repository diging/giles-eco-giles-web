package edu.asu.diging.gilesecosystem.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.asu.diging.gilesecosystem.septemberutil.service.impl.SystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Configuration
public class SystemMessageHandlerConfig {
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Bean
    public SystemMessageHandler getMessageHandler() {
        return new SystemMessageHandler(propertyManager.getProperty(Properties.APPLICATION_ID));
    }

}
