package edu.asu.diging.gilesecosystem.web.service.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesFileStorageException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFileStorageManager;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.service.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.web.service.properties.IPropertiesManager;

@PropertySource("classpath:/config.properties")
@Service
public class PdfFileHandler extends AbstractFileHandler implements IFileTypeHandler {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("tmpStorageManager") 
    private IFileStorageManager storageManager;

    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    @Autowired 
    private IDocumentDatabaseClient documentsDbClient;
    
    @Autowired
    private IRequestFactory<IStorageRequest, StorageRequest> requestFactory;
    
    @Autowired
    private IRequestProducer requestProducer;
    
    @Autowired
    private IPropertiesManager propertyManager;
    
    
    @Override
    public List<String> getHandledFileTypes() {
        List<String> types = new ArrayList<String>();
        types.add(MediaType.APPLICATION_PDF_VALUE);
        return types;
    }
    
    @Override
    public FileType getHandledFileType() {
        return FileType.PDF;
    }

    @Override
    public boolean processFile(String username, IFile file, IDocument document,
            IUpload upload, byte[] content) throws GilesFileStorageException {
        
        return false;
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        String directory = storageManager.getFileFolderPath(
                file.getUsername(), file.getUploadId(), file.getDocumentId());
        return directory + File.separator + file.getFilename();
    }

    @Override
    public String getFileUrl(IFile file) {
        String gilesUrl = propertyManager.getProperty(IPropertiesManager.GILES_URL).trim();
        String pdfEndpoint = propertyManager.getProperty(IPropertiesManager.GILES_FILE_ENDPOINT).trim();
        String contentSuffix = propertyManager.getProperty(IPropertiesManager.GILES_FILE_CONTENT_SUFFIX).trim();
        
        return gilesUrl + pdfEndpoint + file.getId() + contentSuffix;
    }

    @Override
    protected IFileStorageManager getStorageManager() {
        return storageManager;
    }

}
