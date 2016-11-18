package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.ImageExtractionRequest;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.ProcessingPhaseName;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;

@Service
public class ImageExtractionRequestPhase extends ProcessingPhase<IProcessingInfo> {

    @Autowired
    private IRequestFactory<IImageExtractionRequest, ImageExtractionRequest> requestFactory;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @PostConstruct
    public void init() {
        requestFactory.config(ImageExtractionRequest.class);
    }
    
    @Override
    public ProcessingPhaseName getPhaseName() {
        return ProcessingPhaseName.IMAGE_EXTRACTION;
    }

    @Override
    protected IRequest createRequest(IFile file, IProcessingInfo info)
            throws GilesProcessingException {
        if (!file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
            return null;
        }
        
        IImageExtractionRequest request;
        try {
            request = requestFactory.createRequest(file.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new GilesProcessingException(e);
        }
          
        request.setDocumentId(file.getDocumentId());
        request.setDownloadUrl(file.getDownloadUrl());
        request.setRequestId(file.getRequestId());
        request.setStatus(RequestStatus.SUBMITTED);
        request.setFilename(file.getFilename());
        
        return request;
    }

    @Override
    protected String getTopic() {
        return propertyManager.getProperty(IPropertiesManager.KAFKA_TOPIC_IMAGE_EXTRACTION_REQUEST);
    }

    @Override
    protected ProcessingStatus getCompletedStatus() {
        return ProcessingStatus.IMAGE_EXTRACTION_COMPLETE;
    }

    @Override
    protected void cleanup(IFile file) {
        // nothing to do here
    }
}
