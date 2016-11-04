package edu.asu.diging.gilesecosystem.web.service.kafka;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.exceptions.MessageCreationException;

public interface IRequestProducer {

    public abstract void sendRequest(IRequest request, String topic) throws MessageCreationException;

}