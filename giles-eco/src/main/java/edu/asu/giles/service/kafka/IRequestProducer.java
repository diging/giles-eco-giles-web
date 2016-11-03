package edu.asu.giles.service.kafka;

import edu.asu.giles.exceptions.MessageCreationException;
import edu.asu.giles.service.requests.IRequest;

public interface IRequestProducer {

    public abstract void sendRequest(IRequest request, String topic) throws MessageCreationException;

}