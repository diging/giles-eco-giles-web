package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedOCRRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedOCRRequest;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.Page;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedOCRProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

@Service
public class CompletedOCRProcessor extends ACompletedExtractionProcessor implements RequestProcessor<ICompletedOCRRequest>, ICompletedOCRProcessor {

    public final static String REQUEST_PREFIX = "STOCRREQ";
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalFileService filesService;
     
    @Autowired
    private IProcessingCoordinator processCoordinator;
    
    @Autowired
    private IPropertiesManager propertiesManager;
   
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedTextExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedTextExtractionRequest)
     */
    @Override
    public void processRequest(ICompletedOCRRequest request) {
        IDocument document = documentService.getDocument(request.getDocumentId());
        IFile file = filesService.getFileById(document.getUploadedFileId());
        
        Map<String, IPage> pages = getPageMap(document.getPages());
        IFile pageText = createFile(file, document, MediaType.TEXT_PLAIN_VALUE, request.getSize(), request.getTextFilename(), REQUEST_PREFIX);
       
        try {
            filesService.saveFile(pageText);
        } catch (UnstorableObjectException e) {
            // should never happen, we're setting the id
            logger.error("Could not store file.", e);
        }
        
        // we are looking for the image that was ocred
        IPage documentPage = pages.get(request.getFilename());
        if (documentPage == null) {
            // FIXME what about page nr
            documentPage = new Page();
            document.getPages().add(documentPage);
            documentPage.setDocument(document);
        }
        documentPage.setOcrFileId(pageText.getId());
        
        if (request.getDownloadPath() != null && !request.getDownloadPath().isEmpty()
                && request.getDownloadUrl() != null & !request.getDownloadUrl().isEmpty()) {
            request.setStatus(RequestStatus.COMPLETE);
            sendRequest(pageText, request.getDownloadPath(), request.getDownloadUrl(), FileType.TEXT);
        } else {
            request.setStatus(RequestStatus.FAILED);
        }
        
        markRequestComplete(request);
    
        file.setProcessingStatus(ProcessingStatus.OCR_COMPLETE);
        
        try {
            filesService.saveFile(file);
        } catch (UnstorableObjectException e) {
            logger.error("Could not store file.", e);
            // fail silently...
            // this should never happen
        }
        
        try {
            documentService.saveDocument(document);
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
        return propertiesManager.getProperty(Properties.KAFKA_TOPIC_OCR_COMPLETE_REQUEST);
    }

    @Override
    public Class<? extends ICompletedOCRRequest> getRequestClass() {
        return CompletedOCRRequest.class;
    }
    
    /**
     * 
     * This method maps pages to the filename of the image file of a page.
     * @param pages List of pages to be mapped
     * @return A map of the form imageFilename -> page
     */
    private Map<String, IPage> getPageMap(List<IPage> pages) {
        Map<String, IPage> pageMap = new HashMap<>();
        for (IPage page : pages) {
            String imageFileId = page.getImageFileId();
            IFile file = filesService.getFileById(imageFileId);
            
            if (file != null) {
                pageMap.put(file.getFilename(), page);
            }
        }
        return pageMap;
    }
}
