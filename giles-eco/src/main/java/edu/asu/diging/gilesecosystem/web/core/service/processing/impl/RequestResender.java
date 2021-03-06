package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.impl.ResendingResult;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IRequestResender;

@Service
public class RequestResender implements IRequestResender {
    
    @Autowired
    private ApplicationContext ctx;
    
    @Autowired
    private ITransactionalProcessingRequestService requestService;
    
    @Autowired
    private ITransactionalDocumentService documentService;

    @Autowired
    private ISystemMessageHandler messageHandler;
    
    private Map<Class<? extends IRequest>, ProcessingPhase<? extends IProcessingInfo>> phaseMap;
    
    @SuppressWarnings("rawtypes")
    @PostConstruct
    public void init() {
        phaseMap = new HashMap<Class<? extends IRequest>, ProcessingPhase<? extends IProcessingInfo>>();
        Map<String, ProcessingPhase> ctxMap = ctx.getBeansOfType(ProcessingPhase.class);
        Iterator<Entry<String, ProcessingPhase>> iter = ctxMap.entrySet().iterator();
        
        while(iter.hasNext()){
            Entry<String, ProcessingPhase> handlerEntry = iter.next();
            ProcessingPhase phase = (ProcessingPhase) handlerEntry.getValue();
            if (phase.getSupportedRequestType() != null) {
                phaseMap.put(phase.getSupportedRequestType(), phase);   
            }
        }
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.IResendRequestManager#resendRequests()
     */
    @Override
    @Async
    public Future<ResendingResult> resendRequests() {
        int counter = 0;
        List<IProcessingRequest> requests = requestService.getIncompleteRequests();
        for (IProcessingRequest request : requests) {
            IRequest sentRequest = request.getSentRequest();
            ProcessingPhase<?> phase = phaseMap.get(sentRequest.getClass());
            IDocument doc = documentService.getDocument(request.getDocumentId());
            if (doc != null) {
                try {
                    phase.sendRequest(sentRequest, doc);
                    counter++;
                } catch (GilesProcessingException e) {
                    // FIXME: send to september
                    messageHandler.handleMessage("Could not send request.", e, MessageType.ERROR);
                }
            }
        }
        return new AsyncResult<ResendingResult>(new ResendingResult(counter, ZonedDateTime.now()));
    }
}
