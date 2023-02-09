package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedImageExtractionRequest;
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
import edu.asu.diging.gilesecosystem.web.core.service.processing.ICompletedImageExtractionProcessor;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class CompletedImageExtractionProcessor extends ACompletedExtractionProcessor implements RequestProcessor<ICompletedImageExtractionRequest>, ICompletedImageExtractionProcessor {

    public final static String REQUEST_PREFIX = "IMGREQ";
    
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
    public void processRequest(ICompletedImageExtractionRequest request) {
        IDocument document = documentService.getDocument(request.getDocumentId());
        IFile file = filesService.getFileById(document.getUploadedFileId());
        
        Map<Integer, IPage> pages = new HashMap<>();
        document.getPages().forEach(page -> pages.put(page.getPageNr(), page));
        
        if (request.getPages() != null ) {
            for (edu.asu.diging.gilesecosystem.requests.impl.Page page : request.getPages()) {
                IPage documentPage = pages.get(page.getPageNr());
                IFile pageText;
                if(documentPage != null && documentPage.getImageFileId() != null && !documentPage.getImageFileId().isEmpty()) {
                    pageText = filesService.getFileById(documentPage.getImageFileId());
                } else {
                    pageText = createFile(file, document, page.getContentType(), page.getSize(), page.getFilename(), REQUEST_PREFIX);
                    try {
                        filesService.saveFile(pageText);
                    } catch (UnstorableObjectException e) {
                        // should never happen, we're setting the id
                        messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
                    }
                }    
               
                if (documentPage == null) {
                    documentPage = new Page();
                    documentPage.setPageNr(page.getPageNr());
                    document.getPages().add(documentPage);
                    documentPage.setDocument(document);
                }
                documentPage.setImageFileId(pageText.getId());
                // this is a hack, ideally we would map the statuses somewhere, but well...
                documentPage.setImageFileStatus(PageStatus.valueOf(page.getStatus().toString()));
                documentPage.setImageFileErrorMsg(page.getErrorMsg());
                
                if (page.getStatus() == edu.asu.diging.gilesecosystem.requests.PageStatus.COMPLETE) {
                    sendStorageRequest(pageText, page.getPathToFile(), page.getDownloadUrl(), FileType.IMAGE);
                }
           }
        } 
        
        markRequestComplete(request);
        
        file.setProcessingStatus(ProcessingStatus.IMAGE_EXTRACTION_COMPLETE);
        
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
        return propertiesManager.getProperty(Properties.KAFKA_TOPIC_IMAGE_EXTRACTION_COMPLETE_REQUEST);
    }

    @Override
    public Class<? extends ICompletedImageExtractionRequest> getRequestClass() {
        return CompletedImageExtractionRequest.class;
    }
}
