package edu.asu.diging.gilesecosystem.web.core.kafka;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;

@PropertySource("classpath:/config.properties")
public class KafkaDeletionCompletionProcessingListner {

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Autowired
    private IDeleteDocumentService deleteDocumentService;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalProcessingRequestService processingRequestService;
    
    @KafkaListener(id = "giles.deletion.topic.listener", topics = "${topic_delete_storage_request_complete}")
    public void receiveDeletionCompletedMessageMessage(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        ObjectMapper mapper = new ObjectMapper();
        ICompletedStorageDeletionRequest request = null;
        try {
            request = mapper.readValue(message, CompletedStorageDeletionRequest.class);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not unmarshall request.", e, MessageType.ERROR);
            return;
        }
        if (request.getStatus().equals(RequestStatus.FAILED)) {
            List<IProcessingRequest> pRequests = processingRequestService.getProcRequestsByRequestId(request.getRequestId());
            for (IProcessingRequest pReq : pRequests) {
                pReq.setRequestStatus(request.getStatus());
                try {
                    processingRequestService.save(pReq);
                } catch (UnstorableObjectException e) {
                    // should never happen
                    messageHandler.handleMessage("Could not store request.", e, MessageType.ERROR);
                }
            }
            return;
        }
        deleteDocumentService.deleteDocumentAfterStorageDeletion(documentService.getDocument(request.getDocumentId()));
    }
}
