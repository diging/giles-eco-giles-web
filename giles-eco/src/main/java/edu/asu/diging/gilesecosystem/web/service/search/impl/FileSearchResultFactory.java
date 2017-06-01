package edu.asu.diging.gilesecosystem.web.service.search.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.IPage;
import edu.asu.diging.gilesecosystem.web.rest.FilesController;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.service.search.FileSearchResult;
import edu.asu.diging.gilesecosystem.web.service.search.IFileSearchResultFactory;

@Component
public class FileSearchResultFactory implements IFileSearchResultFactory {
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalFileService fileService;

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.search.impl.IFileSearchResultFactory#createSearchResult(java.lang.String)
     */
    @Override
    public FileSearchResult createSearchResult(String fileId) {
        IFile file = fileService.getFileById(fileId);
        
        if (file == null) {
            return null;
        }
        
        FileSearchResult result = new FileSearchResult();
        result.setAccess(file.getAccess());
        result.setContentType(file.getContentType());
        result.setDocumentId(file.getDocumentId());
        result.setFilename(file.getFilename());
        result.setId(file.getId());
        result.setSize(file.getSize());
        result.setUploadDate(file.getUploadDate());
        result.setUploadId(file.getUploadId());
        
        result.setDocumentUrl(propertiesManager.getProperty(Properties.GILES_URL) + FilesController.GET_DOCUMENT_PATH.replace(FilesController.DOCUMENT_ID_PLACEHOLDER, file.getDocumentId()));
        result.setUrl(propertiesManager.getProperty(Properties.GILES_URL) + FilesController.DOWNLOAD_FILE_URL.replace(FilesController.FILE_ID_PLACEHOLDER, file.getId()));
        
        IDocument document = documentService.getDocument(file.getDocumentId());
        if (document == null) {
            return result;
        }
        
        if (document.getExtractedTextFileId().equals(fileId)) {
            // if it's found in the extracted text
            result.setPage(-1);
        } else {
            // if at any point not just text files get index, this should cover all cases 
            if (document.getPages() != null) {
                Optional<IPage> optional = document.getPages().stream().filter(p -> p.getTextFileId().equals(fileId) || p.getOcrFileId().equals(fileId) || p.getImageFileId().equals(fileId)).findFirst();
                if (optional.isPresent()) {
                    IPage page = optional.get();
                    result.setPage(page.getPageNr());
                }
            }
        }
        
        return result;
    }
}
