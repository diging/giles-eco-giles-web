package edu.asu.diging.gilesecosystem.web.service.processing.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.ICompletedImageExtractionRequest;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.Page;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.processing.ICompletedImageExtractionProcessor;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;

@Service
public class CompletedImageExtractionProcessor extends ACompletedExtractionProcessor implements ICompletedImageExtractionProcessor {

    public final static String REQUEST_PREFIX = "IMGREQ";
    
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
     * @see edu.asu.diging.gilesecosystem.web.service.processing.impl.ICompletedImageExtractionProcessor#processCompletedRequest(edu.asu.diging.gilesecosystem.requests.ICompletedImageExtractionRequest)
     */
    @Override
    public void processCompletedRequest(ICompletedImageExtractionRequest request) {
        IDocument document = docsDbClient.getDocumentById(request.getDocumentId());
        IFile file = filesDbClient.getFileById(document.getUploadedFileId());
        
        Map<Integer, IPage> pages = new HashMap<>();
        document.getPages().forEach(page -> pages.put(page.getPageNr(), page));
        
        if (request.getPages() != null ) {
            for (edu.asu.diging.gilesecosystem.requests.impl.Page page : request.getPages()) {
                IFile pageText = createFile(file, document, page.getContentType(), page.getSize(), page.getFilename(), REQUEST_PREFIX);
               
                try {
                    filesDbClient.saveFile(pageText);
                } catch (UnstorableObjectException e) {
                    // should never happen, we're setting the id
                    logger.error("Could not store file.", e);
                }
                
                IPage documentPage = pages.get(page.getPageNr());
                if (documentPage == null) {
                    documentPage = new Page();
                    documentPage.setPageNr(page.getPageNr());
                    document.getPages().add(documentPage);
                }
                documentPage.setImageFileId(pageText.getId());
                
                sendRequest(pageText, page.getPathToFile(), page.getDownloadUrl(), FileType.IMAGE);
            }
        }
        
        file.setProcessingStatus(ProcessingStatus.IMAGE_EXTRACTION_COMPLETE);
        
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
