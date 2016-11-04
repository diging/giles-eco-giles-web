package edu.asu.diging.gilesecosystem.web.service.kafka.impl;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.web.service.kafka.IJsonMessageCreator;

@Component
public class JsonMessageCreator implements IJsonMessageCreator {

    /* (non-Javadoc)
     * @see edu.asu.giles.service.kafka.impl.IJsonMessageCreator#createMessage(edu.asu.giles.service.requests.IRequest)
     */
    @Override
    public String createMessage(IRequest request) throws MessageCreationException {
        ObjectMapper mapper = new ObjectMapper(); 
        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new MessageCreationException("Could not create JSON.", e);
        }
    }
}
