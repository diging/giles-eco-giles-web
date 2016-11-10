package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.service.processing.IDistributedStorageManager;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingPhase;
import edu.asu.diging.gilesecosystem.web.service.processing.ProcessingPhaseName;

@Service
public class FileProcessingCoordinator implements IProcessingCoordinator {
    
    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private IDistributedStorageManager distributedStorageManager;
    
    private Map<ProcessingPhaseName, IProcessingPhase<IProcessingInfo>> processingPhases;
    private Map<ProcessingStatus, ProcessingPhaseName> processChain;
    
    @SuppressWarnings(value="rawtypes")
    @PostConstruct
    public void init() {
        processingPhases = new HashMap<ProcessingPhaseName, IProcessingPhase<IProcessingInfo>>();
        
        Map<String, IProcessingPhase> ctxMap = ctx.getBeansOfType(IProcessingPhase.class);
        Iterator<Entry<String, IProcessingPhase>> iter = ctxMap.entrySet().iterator();
        
        while(iter.hasNext()){
            Entry<String, IProcessingPhase> handlerEntry = iter.next();
            IProcessingPhase phase = (IProcessingPhase) handlerEntry.getValue();
            processingPhases.put(phase.getPhaseName(), phase);
        }
        
        processChain = new HashMap<>();
        processChain.put(ProcessingStatus.UNPROCESSED, ProcessingPhaseName.STORAGE);
        processChain.put(ProcessingStatus.STORED, ProcessingPhaseName.TEXT_EXTRACTION);
        processChain.put(ProcessingStatus.TEXT_EXTRACTION_COMPLETE, ProcessingPhaseName.IMAGE_EXTRACTION);
        processChain.put(ProcessingStatus.IMAGE_EXTRACTION_COMPLETE, ProcessingPhaseName.OCR);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.IProcessingCoordinator#processFile(edu.asu.diging.gilesecosystem.web.core.IFile, edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo)
     */
    @Override
    public RequestStatus processFile(IFile file, IProcessingInfo info) throws GilesProcessingException {
        IProcessingPhase<IProcessingInfo> phase = processingPhases.get(processChain.get(file.getProcessingStatus()));
        return phase.process(file, info);
    }
}
