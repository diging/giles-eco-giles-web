package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IOCRRequest;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.OCRRequest;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.ProcessingPhaseName;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;

@Service
public class OCRRequestPhase extends ProcessingPhase<IProcessingInfo> {

    @Autowired
    private IRequestFactory<IOCRRequest, OCRRequest> requestFactory;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @PostConstruct
    public void init() {
        requestFactory.config(OCRRequest.class);
    }
    
    @Override
    public ProcessingPhaseName getPhaseName() {
        return ProcessingPhaseName.OCR;
    }

    @Override
    protected IRequest createRequest(IFile file, IProcessingInfo info)
            throws GilesProcessingException {
        if (!file.getContentType().startsWith("image/")) {
            return null;
        }
        
        IOCRRequest request;
        try {
            request = requestFactory.createRequest(file.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new GilesProcessingException(e);
        }
          
        request.setDocumentId(file.getDocumentId());
        request.setDownloadUrl(file.getDownloadUrl());
        request.setDownloadPath(file.getFilepath());
        request.setRequestId(file.getRequestId());
        request.setStatus(RequestStatus.SUBMITTED);
        request.setFilename(file.getFilename());
        request.setFileid(file.getId());
        
        return request;
    }

    @Override
    protected String getTopic() {
        return propertyManager.getProperty(IPropertiesManager.KAFKA_TOPIC_OCR_REQUEST);
    }

    @Override
    protected ProcessingStatus getCompletedStatus() {
        return ProcessingStatus.OCR_COMPLETE;
    }

    @Override
    protected void cleanup(IFile file) {
        // nothing to do here
    }
}
