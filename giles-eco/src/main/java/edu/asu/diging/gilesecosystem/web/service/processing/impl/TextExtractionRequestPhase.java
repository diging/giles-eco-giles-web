package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.ITextExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.TextExtractionRequest;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.nepomuk.INepomukUrlService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.ProcessingPhaseName;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class TextExtractionRequestPhase extends ProcessingPhase<IProcessingInfo> {
    
    public final static String REQUEST_PREFIX = "TEEXREQ";
    
    @Autowired
    private ITransactionalFileService filesService;
    
    @Autowired
    private IRequestFactory<ITextExtractionRequest, TextExtractionRequest> requestFactory;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    @Autowired
    private INepomukUrlService nepomukService;
    
    @PostConstruct
    public void init() {
        requestFactory.config(TextExtractionRequest.class);
    }
   
    @Override
    public IRequest createRequest(IFile file, IProcessingInfo info) throws GilesProcessingException {
        if (!file.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
            return null;
        }
        
        ITextExtractionRequest request;
        try {
            request = requestFactory.createRequest(filesService.generateRequestId(REQUEST_PREFIX), file.getUploadId());
        } catch (InstantiationException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new GilesProcessingException(e);
        }
          
        request.setDocumentId(file.getDocumentId());
		if (nepomukService.getFileDownloadPath(file) == null) {
			request.setStatus(RequestStatus.FAILED);
		} else {
			request.setStatus(RequestStatus.SUBMITTED);
		}
        request.setDownloadUrl(nepomukService.getFileDownloadPath(file));
        request.setFilename(file.getFilename());
        return request;
    }

    @Override
    public ProcessingPhaseName getPhaseName() {
        return ProcessingPhaseName.TEXT_EXTRACTION;
    }

    @Override
    protected String getTopic() {
        return propertyManager.getProperty(Properties.KAFKA_TOPIC_TEXT_EXTRACTION_REQUEST);
    }

    @Override
    protected ProcessingStatus getCompletedStatus() {
        return ProcessingStatus.TEXT_EXTRACTION_COMPLETE;
    }

    @Override
    protected void postProcessing(IFile file) {
        // nothing to do here
    }

    @Override
    public Class<? extends IRequest> getSupportedRequestType() {
        return TextExtractionRequest.class;
    }
}
