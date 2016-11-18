package edu.asu.diging.gilesecosystem.web.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.ICompletedImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedImageExtractionRequest;
import edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedImageExtractionProcessor;

@PropertySource("classpath:/config.properties")
public class ImageExtractionProcessingListener {

private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICompletedImageExtractionProcessor requestProcessor;
    
    @KafkaListener(id="giles.image_extraction.complete", topics = "${topic_image_extraction_request_complete}")
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ICompletedImageExtractionRequest request = null;
        try {
            request = mapper.readValue(message, CompletedImageExtractionRequest.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall request.", e);
            // FIXME: handle this case
            return;
        }
        
        requestProcessor.processCompletedRequest(request);
    }
}
