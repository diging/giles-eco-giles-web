package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.processing.ProcessingPhaseName;
import edu.asu.diging.gilesecosystem.web.service.upload.IUploadService;

@Service
public class CompletetionProcessingPhase extends ProcessingPhase<IProcessingInfo> {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;
    
    @Autowired
    private IDocumentDatabaseClient docDbClient;
    
    @Autowired
    private IFilesDatabaseClient fileDbClient;
    
    @Autowired
    private IUploadService uploadService;
    
    @Autowired
    private IProcessingRequestsDatabaseClient pReqDbClient;

    @Override
    public ProcessingPhaseName getPhaseName() {
        return ProcessingPhaseName.COMPLETE;
    }

    @Override
    protected IRequest createRequest(IFile file, IProcessingInfo info)
            throws GilesProcessingException {
        return null;
    }

    @Override
    protected String getTopic() {
        return null;
    }

    @Override
    protected ProcessingStatus getCompletedStatus() {
        return ProcessingStatus.COMPLETE;
    }

    @Override
    protected void postProcessing(IFile file) {
        storageManager.deleteFile(file.getUsernameForStorage(), file.getUploadId(), file.getDocumentId(), file.getFilename(), true);
        IDocument document = docDbClient.getDocumentById(file.getDocumentId());
        
        if (document != null) {
            List<IProcessingRequest> requests = pReqDbClient.getRequestByDocumentId(file.getDocumentId());
            
            boolean completed = requests.stream().allMatch(req -> req.getRequestStatus() == RequestStatus.COMPLETE || req.getRequestStatus() == RequestStatus.FAILED);
            
            if (completed) {
                uploadService.updateStatus(file.getDocumentId(), RequestStatus.COMPLETE);
            }
        }
    }
}
