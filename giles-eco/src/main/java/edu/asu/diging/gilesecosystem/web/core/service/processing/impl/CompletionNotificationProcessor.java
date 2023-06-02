package edu.asu.diging.gilesecosystem.web.core.service.processing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.impl.Page;
import edu.asu.diging.gilesecosystem.requests.impl.PageElement;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
import edu.asu.diging.gilesecosystem.web.core.model.ITask;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Task;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

@Service
public class CompletionNotificationProcessor extends ACompletedExtractionProcessor implements RequestProcessor<ICompletionNotificationRequest> {

    @Autowired
    private IPropertiesManager propertiesManager;
    
    @Autowired
    private ITransactionalDocumentService documentService;

    @Autowired
    private ISystemMessageHandler messageHandler;
   
    @Autowired
    private ITransactionalFileService filesService;
    
    @Override
    public String getProcessedTopic() {
        return propertiesManager.getProperty(Properties.KAFKA_TOPIC_COMPLETION_NOTIFICATION_REQUEST);
    }

    @Override
    public void processRequest(ICompletionNotificationRequest request) {
        IDocument document = documentService.getDocument(request.getDocumentId());
        
        IFile file = filesService.getFileById(request.getFileId());
        String fileDownloadUrl = request.getDownloadUrl();
        
        if (document.getTasks() == null) {
            document.setTasks(new ArrayList<ITask>());
        }
        // if there was a new file created
        saveFile(file, request.getStatus(), request.getNotifier(), document, fileDownloadUrl, request.getContentType(), request.getSize(), request.getFilename(), request.isImageExtracted());
        
        
        if (request.getPages() != null && !request.getPages().isEmpty()) {
            Map<Integer, IPage> pageMap = getPageMap(document);
            for (Page page : request.getPages()) {
                IPage documentPage = pageMap.get(page.getPageNr());
                if(documentPage == null) {
                    documentPage = new edu.asu.diging.gilesecosystem.web.core.model.impl.Page();
                    documentPage.setPageNr(page.getPageNr());
                    document.getPages().add(documentPage);
                    documentPage.setDocument(document);
                }
                if (documentPage.getAdditionalFileIds() == null) {
                    documentPage.setAdditionalFileIds(new ArrayList<>());
                }
                document.getPages().add(documentPage);
                
                IFile additionalFile = saveFile(file, request.getStatus(), request.getNotifier(), document, page.getDownloadUrl(), page.getContentType(), page.getSize(), page.getFilename(), request.isImageExtracted());  
                if (additionalFile != null) {
                    documentPage.getAdditionalFileIds().add(additionalFile.getId());
                }
                for (PageElement element : page.getPageElements()) {
                    IFile elementFile = saveFile(file, request.getStatus(), request.getNotifier(), document, element.getDownloadUrl(), element.getContentType(), element.getSize(), element.getFilename(), request.isImageExtracted());
                    if (elementFile != null) {
                        documentPage.getAdditionalFileIds().add(elementFile.getId());
                    }
                }
                
               
            }
        }
        
        try {
            documentService.saveDocument(document);
        } catch (UnstorableObjectException e) {
            // should never happen
            messageHandler.handleMessage("Could not store document.", e, MessageType.ERROR);
        }
     }
    
    private Map<Integer, IPage> getPageMap(IDocument doc) {
        Map<Integer, IPage> pageMap = new HashMap<>();
        for (IPage page : doc.getPages()) {
            pageMap.put(page.getPageNr(), page);
        }
        return pageMap;
    }

    public IFile saveFile(IFile file, RequestStatus status, String notifier, IDocument document, String downloadUrl, String contentType, long size, String filename, boolean imageExtracted) {
        // if there was a new file created
        if (downloadUrl != null && !downloadUrl.isEmpty()) {
            IFile additionalFile = createFile(file, document, contentType, size, filename, REQUEST_PREFIX);
            
            try {
                filesService.saveFile(additionalFile);
            } catch (UnstorableObjectException e) {
                // should never happen, we're setting the id
                messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
            }
            
            FileType fileType = FileType.OTHER;
            if (contentType != null) {
                if (contentType.equals(MediaType.TEXT_PLAIN_VALUE)) {
                    fileType = FileType.TEXT;
                } else if (contentType.startsWith("image/")) {
                    fileType = FileType.IMAGE;
                } else if (contentType.equals(MediaType.APPLICATION_PDF_VALUE)) {
                    fileType = FileType.PDF;
                }
            }
            if (fileType.equals(FileType.IMAGE) && imageExtracted) {
                sendStorageRequest(additionalFile, "", downloadUrl, fileType, imageExtracted);
            } else {
                sendStorageRequest(additionalFile, "", downloadUrl, fileType);
            }
            
            ITask task = new Task();
            task.setFileId(file.getId());
            task.setStatus(status);
            task.setTaskHandlerId(notifier);
            if (additionalFile != null) {
                task.setResultFileId(additionalFile.getId());
            }
            document.getTasks().add(task);
            return additionalFile;
        }
        
        return null;
    }

    @Override
    public Class<? extends ICompletionNotificationRequest> getRequestClass() {
        return CompletionNotificationRequest.class;
    }

}
