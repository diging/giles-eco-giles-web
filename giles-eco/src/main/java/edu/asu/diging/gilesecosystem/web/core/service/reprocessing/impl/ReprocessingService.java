package edu.asu.diging.gilesecosystem.web.core.service.reprocessing.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.impl.StorageRequestProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

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

    @Override
    public void reprocessDocument(IDocument document) {
        changeProcessingStatusForFiles(document);
        try {
            processCoordinator.processFile(fileService.getFileById(document.getUploadedFileId()), getStorageInfo(document));
        } catch (GilesProcessingException e) {
            messageHandler.handleMessage("Could not store uploaded files.", e, MessageType.ERROR);
        }
    }

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

    private void changeProcessingStatusForFiles(IDocument document) {
        List<IFile> files = filesManager.getFilesOfDocument(document);
        for(IFile file : files) {
            filesManager.changeFileProcessingStatus(file, ProcessingStatus.UNPROCESSED);
        }
    }

}
