package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.Page;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedTextExtractionProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;

@Service
public class CompletedTextExtractionProcessor extends ACompletedExtractionProcessor implements ICompletedTextExtractionProcessor {
    
    public final static String REQUEST_PREFIX = "TXTREQ";
      
    @Autowired
    private IDocumentDatabaseClient docsDbClient;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
     
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    @Override
    public void processCompletedRequest(ICompletedTextExtractionRequest request) {
        IDocument document = docsDbClient.getDocumentById(request.getDocumentId());
        IFile file = filesDbClient.getFileById(document.getUploadedFileId());
        
        String completeTextDownload = request.getDownloadUrl();
        // text was extracted
        if (completeTextDownload != null && !completeTextDownload.isEmpty()) {
            IFile completeText = createFile(file, document, MediaType.TEXT_PLAIN_VALUE, request.getSize(), request.getTextFilename(), REQUEST_PREFIX);
            
            try {
                filesDbClient.saveFile(completeText);
            } catch (UnstorableObjectException e) {
                // should never happen, we're setting the id
                logger.error("Could not store file.", e);
            }
            
            document.setExtractedTextFileId(completeText.getId());
            
            sendRequest(completeText, request.getDownloadPath(), request.getDownloadUrl(), FileType.TEXT);
        }
        
        if (request.getPages() != null ) {
            for (edu.asu.diging.gilesecosystem.requests.impl.Page page : request.getPages()) {
                IFile pageText = createFile(file, document, MediaType.TEXT_PLAIN_VALUE, page.getSize(), page.getFilename(), REQUEST_PREFIX);
               
                try {
                    filesDbClient.saveFile(pageText);
                } catch (UnstorableObjectException e) {
                    // should never happen, we're setting the id
                    logger.error("Could not store file.", e);
                }
                
                IPage documentPage = new Page();
                documentPage.setPageNr(page.getPageNr());
                documentPage.setTextFileId(pageText.getId());
                
                document.getPages().add(documentPage);
                
                sendRequest(pageText, page.getPathToFile(), page.getDownloadUrl(), FileType.TEXT);
            }
        }
        
        file.setProcessingStatus(ProcessingStatus.TEXT_EXTRACTION_COMPLETE);
        
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
