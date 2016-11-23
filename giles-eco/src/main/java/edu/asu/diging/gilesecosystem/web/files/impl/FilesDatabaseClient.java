package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.impl.File;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;

@Transactional("txmanager_files")
@Component
public class FilesDatabaseClient extends DatabaseClient<IFile> implements
        IFilesDatabaseClient {

    @PersistenceContext(unitName="emf_files")
    private EntityManager em;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.IFilesDatabaseClient#addFile(edu.asu.giles.core.impl
     * .File)
     */
    @Override
    public IFile saveFile(IFile file) throws UnstorableObjectException {
       return store(file);
    }

    @Override
    public IFile getFileById(String id) {
        return em.find(File.class, id);
    }

    @Override
    public List<IFile> getFilesByUploadId(String uploadId) {
        return searchByProperty("uploadId", uploadId, File.class);
    }
    
    @Override
    public List<IFile> getFilesByUsername(String username) {
        return searchByProperty("username", username, File.class);
    }
    
    @Override
    public List<IFile> getFilesByPath(String path) {
        return searchByProperty("filepath", path, File.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.IFilesDatabaseClient#getFile(java.lang.String)
     */
    @Override
    public IFile getFile(String filename) {
        List<IFile> files = searchByProperty("filename", filename, File.class);
        if (files != null && !files.isEmpty()) {
            return files.get(0);
        }
        return null;
    }
    
    @Override
    public IFile getFileByRequestId(String requestId) {
        List<IFile> files = searchByProperty("requestId", requestId, File.class);
        if (files != null && !files.isEmpty()) {
            return files.get(0);
        }
        return null;
    }

    @Override
    protected String getIdPrefix() {
        return "FILE";
    }

    @Override
    protected IFile getById(String id) {
        return getFileById(id);
    }

    @Override
    protected EntityManager getClient() {
        return em;
    }

}
