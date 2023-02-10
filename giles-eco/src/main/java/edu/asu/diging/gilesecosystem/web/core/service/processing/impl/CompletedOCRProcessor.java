package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedOCRRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedOCRRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
import edu.asu.diging.gilesecosystem.web.core.model.ITask;
import edu.asu.diging.gilesecosystem.web.core.model.PageStatus;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Page;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Task;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.ICompletedOCRProcessor;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class CompletedOCRProcessor extends ACompletedExtractionProcessor implements RequestProcessor<ICompletedOCRRequest>, ICompletedOCRProcessor {

    public final static String REQUEST_PREFIX = "STOCRREQ";
    public final static String OCR_PROVIDER_ID = "ocr";
    
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
    public void processRequest(ICompletedOCRRequest request) {
        IDocument document = documentService.getDocument(request.getDocumentId());
        IFile file = filesService.getFileById(document.getUploadedFileId());
        Map<String, IPage> pages = getPageMap(document.getPages());
        Map<String, IPage> additionalFilesPagesMap = getAdditionalFilesPageMap(document.getPages());
        IPage documentPage = pages.get(request.getFilename());
        Function<IPage, String> getFileId = docPage -> docPage.getOcrFileId();
        IFile textFile = getFile(documentPage, document, file, MediaType.TEXT_PLAIN_VALUE, request.getSize(), request.getTextFilename(), REQUEST_PREFIX, getFileId);
        
        // we are looking for the image that was ocred
        if (documentPage != null) {
            documentPage.setOcrFileId(textFile.getId());
            if (request.getStatus() != null) {
                documentPage.setOcrFileStatus(PageStatus.valueOf(request.getStatus().toString())); 
            } else {
                documentPage.setOcrFileStatus(PageStatus.COMPLETE);
            }
            documentPage.setOcrFileErrorMsg(request.getErrorMsg());
        } else {
            // maybe its an ocr of an additional file
            documentPage = additionalFilesPagesMap.get(request.getFilename());
            if (documentPage == null) {
                documentPage = new Page();
                document.getPages().add(documentPage);
                documentPage.setDocument(document);
            }
            if (documentPage.getAdditionalFileIds() == null) {
                documentPage.setAdditionalFileIds(new ArrayList<>());
            }
            documentPage.getAdditionalFileIds().add(textFile.getId());
            
            ITask task = new Task();
            task.setFileId(file.getId());
            task.setStatus(request.getStatus());
            task.setTaskHandlerId(OCR_PROVIDER_ID);
            task.setResultFileId(textFile.getId());
            
            document.getTasks().add(task);
        }
        
        
        if (request.getDownloadPath() != null && !request.getDownloadPath().isEmpty()
                && request.getDownloadUrl() != null & !request.getDownloadUrl().isEmpty() && request.getStatus() != RequestStatus.FAILED) {
            request.setStatus(RequestStatus.COMPLETE);
            sendStorageRequest(textFile, request.getDownloadPath(), request.getDownloadUrl(), FileType.TEXT);
        } else {
            request.setStatus(RequestStatus.FAILED);
        }
        
        markRequestComplete(request);
    
        file.setProcessingStatus(ProcessingStatus.OCR_COMPLETE);
        
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
    
    /**
     * 
     * This method maps pages to the filenames of additional files.
     * @param pages List of pages to be mapped
     * @return A map of the form imageFilename -> page
     */
    private Map<String, IPage> getAdditionalFilesPageMap(List<IPage> pages) {
        Map<String, IPage> pageMap = new HashMap<>();
        for (IPage page : pages) {
            for (String additionalFileId : page.getAdditionalFileIds()) {
                IFile additionalFile = filesService.getFileById(additionalFileId);
                
                if (additionalFile != null) {
                    pageMap.put(additionalFile.getFilename(), page);
                }  
            }
        }
        return pageMap;
    }
}
