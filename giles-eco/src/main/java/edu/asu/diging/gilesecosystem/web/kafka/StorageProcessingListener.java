package edu.asu.diging.gilesecosystem.web.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedStorageRequest;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedStorageRequestProcessor;

@PropertySource("classpath:/config.properties")
public class StorageProcessingListener {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICompletedStorageRequestProcessor requestProcessor;
    
    @KafkaListener(topics = "${topic_storage_request_complete}")
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ICompletedStorageRequest request = null;
        try {
            request = mapper.readValue(message, CompletedStorageRequest.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall request.", e);
            // FIXME: handel this case
            return;
        }
        
        requestProcessor.processCompletedRequest(request);
    }
}
