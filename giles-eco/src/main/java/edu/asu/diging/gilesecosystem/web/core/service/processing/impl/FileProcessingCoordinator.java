package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingPhase;
import edu.asu.diging.gilesecosystem.web.core.service.processing.ProcessingPhaseName;

@Service
public class FileProcessingCoordinator implements IProcessingCoordinator {
    
    @Autowired
    private ApplicationContext ctx;

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
        processChain.put(ProcessingStatus.OCR_COMPLETE, ProcessingPhaseName.COMPLETE);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.IProcessingCoordinator#processFile(edu.asu.diging.gilesecosystem.web.core.IFile, edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo)
     */
    @Override
    public RequestStatus processFile(IFile file, IProcessingInfo info) throws GilesProcessingException {
        ProcessingPhaseName nextPhase = processChain.get(file.getProcessingStatus());
        IProcessingPhase<IProcessingInfo> phase = processingPhases.get(nextPhase);
        if (phase != null) {
            return phase.process(file, info);
        }
        return null;
    }
}
