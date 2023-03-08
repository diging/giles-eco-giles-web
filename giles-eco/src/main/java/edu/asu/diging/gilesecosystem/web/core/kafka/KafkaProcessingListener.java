package edu.asu.diging.gilesecosystem.web.core.kafka;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.diging.gilesecosystem.requests.ICompletedStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.service.IProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.RequestProcessor;

@PropertySource("classpath:/config.properties")
public class KafkaProcessingListener {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private IProcessingRequestService processingRequestService;

    private Map<String, RequestProcessor<? extends IRequest>> requestProcessors;

    @Autowired
    private ISystemMessageHandler messageHandler;

    @Autowired
    private IDeleteDocumentService deleteDocumentService;
    
    @Autowired
    private ITransactionalDocumentService documentService;

    @PostConstruct
    public void init() {
        requestProcessors = new HashMap<String, RequestProcessor<? extends IRequest>>();
        Map<String, RequestProcessor> ctxMap = ctx.getBeansOfType(RequestProcessor.class);
        Iterator<Entry<String, RequestProcessor>> iter = ctxMap.entrySet().iterator();

        while (iter.hasNext()) {
            Entry<String, RequestProcessor> entry = iter.next();
            requestProcessors.put(entry.getValue().getProcessedTopic(), entry.getValue());
        }
    }

    @Transactional("transactionManager")
    @KafkaListener(id = "giles.listener", topics = { "${topic_storage_request_complete}",
            "${topic_image_extraction_request_complete}", "${topic_orc_request_complete}",
            "${topic_text_extraction_request_complete}", "${topic_completion_notification}" })
    public void receiveMessage(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        RequestProcessor<? extends IRequest> processor = requestProcessors.get(topic);
        // no registered processor
        if (processor == null) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        IRequest request = null;
        try {
            request = mapper.readValue(message, processor.getRequestClass());
        } catch (IOException e) {
            messageHandler.handleMessage("Could not unmarshall request.", e, MessageType.ERROR);
            // FIXME: handle this case
            return;
        }

        processingRequestService.addReceivedRequest(request);
        processor.handleRequest(request);
    }

    @KafkaListener(id = "giles.deletion.topic.listener", topics = "${topic_delete_storage_request_complete}")
    public void receiveDeletionCompletedMessageMessage(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        ObjectMapper mapper = new ObjectMapper();
        
        ICompletedStorageDeletionRequest request = null;
        try {
            request = mapper.readValue(message, CompletedStorageDeletionRequest.class);
        } catch (IOException e) {
            messageHandler.handleMessage("Could not unmarshall request.", e, MessageType.ERROR);
            // FIXME: handle this case
            return;
        }
        deleteDocumentService.deleteDocumentAfterStorageDeletion(documentService.getDocument(request.getDocumentId()));
    }
}
