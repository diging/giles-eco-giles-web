package edu.asu.diging.gilesecosystem.web.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedTextExtractionRequest;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedTextExtractionProcessor;

@PropertySource("classpath:/config.properties")
public class TextExtractionProcessingListener {

private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICompletedTextExtractionProcessor requestProcessor;
    
    @KafkaListener(id="giles.text_extraction.complete", topics = "${topic_text_extraction_request_complete}")
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ICompletedTextExtractionRequest request = null;
        try {
            request = mapper.readValue(message, CompletedTextExtractionRequest.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall request.", e);
            // FIXME: handle this case
            return;
        }
        
        requestProcessor.processCompletedRequest(request);
    }
}
