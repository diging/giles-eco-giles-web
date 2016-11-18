package edu.asu.diging.gilesecosystem.web.kafka;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.ICompletedOCRRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedOCRRequest;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedOCRProcessor;

@PropertySource("classpath:/config.properties")
public class OCRProcessingListener {

private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ICompletedOCRProcessor requestProcessor;
    
    @KafkaListener(id="giles.ocr.complete", topics = "${topic_orc_request_complete}")
    public void receiveMessage(String message) {
        ObjectMapper mapper = new ObjectMapper();
        ICompletedOCRRequest request = null;
        try {
            request = mapper.readValue(message, CompletedOCRRequest.class);
        } catch (IOException e) {
            logger.error("Could not unmarshall request.", e);
            // FIXME: handle this case
            return;
        }
        
        requestProcessor.processCompletedRequest(request);
    }
}
