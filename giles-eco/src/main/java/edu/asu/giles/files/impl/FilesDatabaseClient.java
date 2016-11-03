package edu.asu.giles.files.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.db4o.ObjectContainer;

import edu.asu.giles.core.IFile;
import edu.asu.giles.core.impl.File;
import edu.asu.giles.db4o.impl.DatabaseClient;
import edu.asu.giles.db4o.impl.DatabaseManager;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.IFilesDatabaseClient;

@Component
public class FilesDatabaseClient extends DatabaseClient<IFile> implements
        IFilesDatabaseClient {

    private ObjectContainer client;

    @Autowired
    @Qualifier("filesDatabaseManager")
    private DatabaseManager userDatabase;

    @PostConstruct
    public void init() {
        client = userDatabase.getClient();
    }

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
        IFile file = new File();
        file.setId(id);
        return queryByExampleGetFirst(file);
    }

    @Override
    public List<IFile> getFilesByUploadId(String uploadId) {
        IFile file = new File();
        file.setUploadId(uploadId);
        return getFilesByExample(file);
    }
    
    @Override
    public List<IFile> getFilesByUsername(String username) {
        IFile file = new File();
        file.setUsername(username);
        return getFilesByExample(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.IFilesDatabaseClient#getFile(java.lang.String)
     */
    @Override
    public IFile getFile(String filename) {
        IFile file = new File(filename);
        return queryByExampleGetFirst(file);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.IFilesDatabaseClient#getFilesByExample(edu.asu.giles
     * .core.impl.File)
     */
    @Override
    public List<IFile> getFilesByExample(IFile file) {
        return client.queryByExample(file);
    }

    @Override
    protected String getIdPrefix() {
        return "FILE";
    }

    @Override
    protected Object getById(String id) {
        return getFileById(id);
    }

    @Override
    protected ObjectContainer getClient() {
        return client;
    }

}
