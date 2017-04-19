package edu.asu.diging.gilesecosystem.web.service.core.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;

@Service
@Transactional("transactionManager")
public class TransactionalFileService implements ITransactionalFileService {
    
    private final static String REQUEST_PREFIX = "STREQ";

    @Autowired
    private IFilesDatabaseClient filesDbClient;
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.core.impl.ITransactionalFileService#getRequestId()
     */
    @Override
    public String generateRequestId() {
        return filesDbClient.generateId(REQUEST_PREFIX, filesDbClient::getFileByRequestId);
    }
    
    @Override
    public String generateRequestId(String prefix) {
        return filesDbClient.generateId(prefix, filesDbClient::getFileByRequestId);
    }
    
    @Override
    public String generateFileId() {
        return filesDbClient.generateId();
    }
    
    @Override
    public void saveFile(IFile file) throws UnstorableObjectException {
        filesDbClient.saveFile(file);
    }
    
    @Override
    public IFile getFileById(String id) {
        if (id == null) {
            return null;
        }
        return filesDbClient.getFileById(id);
    }
    
    @Override
    public IFile getFileByPath(String path) {
        IFile file = new edu.asu.diging.gilesecosystem.web.domain.impl.File();
        file.setFilepath(path);

        List<IFile> files = filesDbClient.getFilesByPath(path);
        if (files == null || files.isEmpty()) {
            return null;
        }

        return files.get(0);
    }
    
    @Override
    public IFile getFileByRequestId(String requestId) {
        return filesDbClient.getFileByRequestId(requestId);
    }
}
