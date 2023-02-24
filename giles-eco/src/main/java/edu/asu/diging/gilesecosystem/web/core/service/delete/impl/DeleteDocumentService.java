package edu.asu.diging.gilesecosystem.web.core.service.delete.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import edu.asu.diging.gilesecosystem.requests.IStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;

public class DeleteDocumentService implements IDeleteDocumentService {
    
    @Autowired
    private IFilesManager filesManager;
    
    @Override
    @Async
    public void deleteDocument(IDocument document) {
        List<IFile> files = filesManager.getFilesOfDocument(document);
        for(IFile file : files) {
            deleteFileFromStorage(file);
        }
    }
    
    public void sendDeleteRequest() {
        
    }
    
    public void deleteFileFromStorage(IFile file) {
        IStorageDeletionRequest storageDeletionRequest;
        
    }

}
