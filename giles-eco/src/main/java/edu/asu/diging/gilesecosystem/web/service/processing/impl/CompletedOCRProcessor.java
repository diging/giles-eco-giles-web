package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedOCRRequest;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.Page;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedOCRProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;

@Service
public class CompletedOCRProcessor extends ACompletedExtractionProcessor implements ICompletedOCRProcessor {

    public final static String REQUEST_PREFIX = "STOCRREQ";
    
    @Autowired
    private IDocumentDatabaseClient docsDbClient;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
     
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedOCRProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedOCRRequest)
     */
    @Override
    public void processCompletedRequest(ICompletedOCRRequest request) {
        IDocument document = docsDbClient.getDocumentById(request.getDocumentId());
        IFile file = filesDbClient.getFileById(document.getUploadedFileId());
        
        Map<String, IPage> pages = new HashMap<>();
        document.getPages().forEach(page -> pages.put(page.getImageFileId(), page));
        
        IFile pageText = createFile(file, document, MediaType.TEXT_PLAIN_VALUE, request.getSize(), request.getTextFilename(), REQUEST_PREFIX);
       
        try {
            filesDbClient.saveFile(pageText);
        } catch (UnstorableObjectException e) {
            // should never happen, we're setting the id
            logger.error("Could not store file.", e);
        }
        
        IPage documentPage = pages.get(request.getFileid());
        if (documentPage == null) {
            // FIXME what about page nr
            documentPage = new Page();
            document.getPages().add(documentPage);
        }
        documentPage.setOcrFileId(pageText.getId());
        
        sendRequest(pageText, request.getDownloadPath(), request.getDownloadUrl(), FileType.TEXT);
    
        file.setProcessingStatus(ProcessingStatus.OCR_COMPLETE);
        
        try {
            filesDbClient.saveFile(file);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file.", e);
            // fail silently...
            // this should never happen
        }
        
        try {
            docsDbClient.saveDocument(document);
        } catch (UnstorableObjectException e) {
            // shoudl never happen
            // report to monitoring app
            logger.error("Could not store document.", e);
        }
        
        try {
            processCoordinator.processFile(file, null);
        } catch (GilesProcessingException e) {
            // FIXME: send to monitoring app
            logger.error("Processing failed.", e);
        }
    }
}
