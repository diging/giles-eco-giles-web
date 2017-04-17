package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedTextExtractionRequest;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.Page;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedTextExtractionProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
@Transactional
public class CompletedTextExtractionProcessor extends ACompletedExtractionProcessor implements RequestProcessor<ICompletedTextExtractionRequest>, ICompletedTextExtractionProcessor {
    
    public final static String REQUEST_PREFIX = "TXTREQ";
      
    @Autowired
    private IDocumentDatabaseClient docsDbClient;
    
    @Autowired
    private IFilesDatabaseClient filesDbClient;
     
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    @Autowired
    private IPropertiesManager propertiesManager;
   
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    @Override
    public void processRequest(ICompletedTextExtractionRequest request) {
        IDocument document = docsDbClient.getDocumentById(request.getDocumentId());
        IFile file = filesDbClient.getFileById(document.getUploadedFileId());
        
        String completeTextDownload = request.getDownloadUrl();
        // text was extracted
        if (completeTextDownload != null && !completeTextDownload.isEmpty()) {
            request.setStatus(RequestStatus.COMPLETE);
            IFile completeText = createFile(file, document, MediaType.TEXT_PLAIN_VALUE, request.getSize(), request.getTextFilename(), REQUEST_PREFIX);
            
            try {
                filesDbClient.saveFile(completeText);
            } catch (UnstorableObjectException e) {
                // should never happen, we're setting the id
                logger.error("Could not store file.", e);
            }
            
            document.setExtractedTextFileId(completeText.getId());
            
            sendRequest(completeText, request.getDownloadPath(), request.getDownloadUrl(), FileType.TEXT);
        } else {
            request.setStatus(RequestStatus.FAILED);
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
                documentPage.setDocument(document);
                documentPage.setPageNr(page.getPageNr());
                documentPage.setTextFileId(pageText.getId());
                
                document.getPages().add(documentPage);
                
                sendRequest(pageText, page.getPathToFile(), page.getDownloadUrl(), FileType.TEXT);
            }
        }
        
        file.setProcessingStatus(ProcessingStatus.TEXT_EXTRACTION_COMPLETE);
        markRequestComplete(request);
        
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

    @Override
    public String getProcessedTopic() {
        return propertiesManager.getProperty(Properties.KAFKA_TOPIC_TEXT_EXTRACTION_COMPLETE_REQUEST);
    }

    @Override
    public Class<? extends CompletedTextExtractionRequest> getRequestClass() {
        return CompletedTextExtractionRequest.class;
    }

}
