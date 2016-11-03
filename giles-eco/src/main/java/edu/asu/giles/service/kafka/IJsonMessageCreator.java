package edu.asu.giles.service.kafka;

import edu.asu.giles.exceptions.MessageCreationException;
import edu.asu.giles.service.requests.IRequest;

public interface IJsonMessageCreator {

    public abstract String createMessage(IRequest request)
            throws MessageCreationException;

}