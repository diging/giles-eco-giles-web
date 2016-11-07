package edu.asu.diging.gilesecosystem.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.requests.kafka.KafkaConfig;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;

@Component
public class GilesKafkaConfig implements KafkaConfig {
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @Override
    public String getHosts() {
        return propertiesManager.getProperty(IPropertiesManager.KAFKA_HOSTS);
    }

}
