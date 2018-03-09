package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.ArrayList;

import javax.print.attribute.standard.Media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.requests.impl.CompletionNotificationRequest;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.ITask;
import edu.asu.diging.gilesecosystem.web.domain.impl.Task;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.service.processing.RequestProcessor;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;

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
        
        IFile file = filesService.getFileById(document.getUploadedFileId());
        String fileDownloadUrl = request.getDownloadUrl();
        // if there was a new file created
        if (fileDownloadUrl != null && !fileDownloadUrl.isEmpty()) {
            IFile additionalFile = createFile(file, document, request.getContentType(), request.getSize(), request.getFilename(), REQUEST_PREFIX);
            
            try {
                filesService.saveFile(additionalFile);
            } catch (UnstorableObjectException e) {
                // should never happen, we're setting the id
                messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
            }
            
            document.setExtractedTextFileId(additionalFile.getId());
            
            FileType fileType = FileType.OTHER;
            if (request.getContentType().equals(MediaType.TEXT_PLAIN_VALUE)) {
                fileType = FileType.TEXT;
            } else if (request.getContentType().startsWith("image/")) {
                fileType = FileType.IMAGE;
            } else if (request.getContentType().equals(MediaType.APPLICATION_PDF_VALUE)) {
                fileType = FileType.PDF;
            }
            sendStorageRequest(additionalFile, request.getDownloadPath(), request.getDownloadUrl(), fileType);
        } 
        
        ITask task = new Task();
        task.setFileId(request.getFileId());
        task.setStatus(request.getStatus());
        task.setTaskHandlerId(request.getNotifier());
        
        if (document.getTasks() == null) {
            document.setTasks(new ArrayList<ITask>());
        }
        
        document.getTasks().add(task);
        try {
            documentService.saveDocument(document);
        } catch (UnstorableObjectException e) {
            // should never happen
            messageHandler.handleMessage("Could not store document.", e, MessageType.ERROR);
        }
     }

    @Override
    public Class<? extends ICompletionNotificationRequest> getRequestClass() {
        return CompletionNotificationRequest.class;
    }

}
