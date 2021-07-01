package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedTextExtractionRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
import edu.asu.diging.gilesecosystem.web.core.model.PageStatus;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Page;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.ICompletedTextExtractionProcessor;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class CompletedTextExtractionProcessor extends ACompletedExtractionProcessor implements RequestProcessor<ICompletedTextExtractionRequest>, ICompletedTextExtractionProcessor {
    
    public final static String REQUEST_PREFIX = "TXTREQ";
      
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalFileService filesService;
     
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    @Autowired
    private IPropertiesManager propertiesManager;

    @Autowired
    private ISystemMessageHandler messageHandler;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    @Override
    public void processRequest(ICompletedTextExtractionRequest request) {
        IDocument document = documentService.getDocument(request.getDocumentId());
        IFile file = filesService.getFileById(document.getUploadedFileId());
        
        String completeTextDownload = request.getDownloadUrl();
        // text was extracted
        if (completeTextDownload != null && !completeTextDownload.isEmpty()) {
            IFile completeText = createFile(file, document, MediaType.TEXT_PLAIN_VALUE, request.getSize(), request.getTextFilename(), REQUEST_PREFIX);
            
            try {
                filesService.saveFile(completeText);
            } catch (UnstorableObjectException e) {
                // should never happen, we're setting the id
                messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
            }
            
            document.setExtractedTextFileId(completeText.getId());
            
            sendStorageRequest(completeText, request.getDownloadPath(), request.getDownloadUrl(), FileType.TEXT);
        } 
        
        if (request.getPages() != null ) {
            for (edu.asu.diging.gilesecosystem.requests.impl.Page page : request.getPages()) {
                IFile pageText = createFile(file, document, MediaType.TEXT_PLAIN_VALUE, page.getSize(), page.getFilename(), REQUEST_PREFIX);
               
                try {
                    filesService.saveFile(pageText);
                } catch (UnstorableObjectException e) {
                    // should never happen, we're setting the id
                    messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
                }
                
                IPage documentPage = new Page();
                documentPage.setDocument(document);
                documentPage.setPageNr(page.getPageNr());
                documentPage.setTextFileId(pageText.getId());
                if (page.getStatus() != null) {
                    documentPage.setTextFileStatus(PageStatus.valueOf(page.getStatus().toString()));
                }
                documentPage.setTextFileErrorMsg(page.getErrorMsg());
                document.getPages().add(documentPage);
                
                sendStorageRequest(pageText, page.getPathToFile(), page.getDownloadUrl(), FileType.TEXT);
            }
        }
        
        file.setProcessingStatus(ProcessingStatus.TEXT_EXTRACTION_COMPLETE);
        markRequestComplete(request);
        
        try {
            filesService.saveFile(file);
        } catch (UnstorableObjectException e) {
            messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
            // fail silently...
            // this should never happen
        }
        
        try {
            documentService.saveDocument(document);
        } catch (UnstorableObjectException e) {
            // shoudl never happen
            // report to monitoring app
            messageHandler.handleMessage("Could not store document.", e, MessageType.ERROR);
        }
        
        try {
            processCoordinator.processFile(file, null);
        } catch (GilesProcessingException e) {
            // FIXME: send to monitoring app
            messageHandler.handleMessage("Processing failed.", e, MessageType.ERROR);
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
