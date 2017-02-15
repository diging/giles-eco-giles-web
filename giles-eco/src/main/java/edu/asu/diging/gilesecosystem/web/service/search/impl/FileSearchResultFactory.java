package edu.asu.diging.gilesecosystem.web.service.search.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.rest.FilesController;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.service.search.FileSearchResult;
import edu.asu.diging.gilesecosystem.web.service.search.IFileSearchResultFactory;

@Component
public class FileSearchResultFactory implements IFileSearchResultFactory {
    
    @Autowired
    private IPropertiesManager propertiesManager;
    
    @Autowired
    private IFilesManager filesManager;

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.search.impl.IFileSearchResultFactory#createSearchResult(java.lang.String)
     */
    @Override
    public FileSearchResult createSearchResult(String fileId) {
        IFile file = filesManager.getFile(fileId);
        
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
        
        IDocument document = filesManager.getDocument(file.getDocumentId());
        
        if (document.getExtractedTextFileId().equals(fileId)) {
            // if it's found in the extracted text
            result.setPage(-1);
        } else {
            // if at any point not just text files get index, this should cover all cases 
            Optional<IPage> optional = document.getPages().stream().filter(p -> p.getTextFileId().equals(fileId) || p.getOcrFileId().equals(fileId) || p.getImageFileId().equals(fileId)).findFirst();
            if (optional.isPresent()) {
                IPage page = optional.get();
                result.setPage(page.getPageNr());
            }
        }
        
        return result;
    }
}
