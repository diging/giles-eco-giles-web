package edu.asu.diging.gilesecosystem.web.core.service.reprocessing.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.repository.ProcessingRequestRepository;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.impl.StorageRequestProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;
/**
 * This class handles the reprocessing of the uploaded document in case of any failures.
*/
@Service
public class ReprocessingService implements IReprocessingService {
    
    @Autowired
    private ITransactionalFileService fileService;
    
    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    @Autowired
    private IUserManager userManager;
    
    @Autowired
    private ISystemMessageHandler messageHandler;
    
    @Autowired
    private IUploadDatabaseClient uploadDatabaseClient;
    
    @Autowired
    private IProcessingRequestsDatabaseClient procReqDbClient;

    @Autowired
    private ProcessingRequestRepository processingRequestRepository;
    
    /**
     * This method initiates the reprocessing of the dcument by marking the files as unprocessed and calling the process coordinator.
     * @param document document which needs to be reprocessed
     * @return 
    */
    
    @Override
    public void reprocessDocument(IDocument document) {
        markFilesAsUnprocessed(document);
        deletePreviousRequests(document);
        try {
            processCoordinator.processFile(fileService.getFileById(document.getUploadedFileId()), getStorageInfo(document));
        } catch (GilesProcessingException e) {
            messageHandler.handleMessage("Could not store uploaded files.", e, MessageType.ERROR);
        }
    }
    
    /**
     * This method returns the storage information of the already created document.
     * @param document document for which we need the storage information
     * @return returns the storage information of the document
    */
    private StorageRequestProcessingInfo getStorageInfo(IDocument document) {
        IFile file = fileService.getFileById(document.getUploadedFileId());
        User user = userManager.findUser(file.getUsername());
        StorageRequestProcessingInfo info = new StorageRequestProcessingInfo();
        info.setContent(filesManager.getFileContent(file));
        info.setDocument(document);
        info.setFile(file);
        info.setProvider(user.getProvider());
        info.setProviderUsername(user.getUserIdOfProvider());
        info.setUpload(uploadDatabaseClient.getUpload(document.getUploadId()));
        return info;
    }
    
    /**
     * This method marks all the files of the document as unprocessed.
     * @param document document who's files status needs to be changed to unprocessed
     * @return 
    */
    private void markFilesAsUnprocessed(IDocument document) {
        List<IFile> files = filesManager.getFilesOfDocument(document);
        for(IFile file : files) {
            filesManager.changeFileProcessingStatus(file, ProcessingStatus.UNPROCESSED);
        }
    }
    
    private void deletePreviousRequests(IDocument document) {
        List<IProcessingRequest> pRequests = procReqDbClient.getRequestByDocumentId(document.getId());
        System.out.println("Diya test " + pRequests);
        for (IProcessingRequest request : pRequests) {
            System.out.println("Diya test " + request);
            processingRequestRepository.deleteById(request.getId());
        }
    }
}
