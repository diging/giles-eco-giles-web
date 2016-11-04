package edu.asu.diging.gilesecosystem.web.service.kafka;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.web.exceptions.MessageCreationException;

public interface IJsonMessageCreator {

    public abstract String createMessage(IRequest request)
            throws MessageCreationException;

}