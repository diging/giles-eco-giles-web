package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.repository.UploadRepository;

@Transactional("transactionManager")
@Service
public class UploadDatabaseClient extends DatabaseClient<IUpload> implements
        IUploadDatabaseClient {

    private final UploadRepository uploadRepository;

    @Autowired
    public UploadDatabaseClient(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }
    
    @Override
    public IUpload saveUpload(IUpload upload) throws IllegalArgumentException {
        return uploadRepository.save(upload);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IUploadDatabaseClient#getUpload(java.lang.String
     * )
     */
    @Override
    public IUpload getUpload(String id) {
        return uploadRepository.findById(id).orElse(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IUploadDatabaseClient#getUploadsForUser(java
     * .lang.String)
     */
    @Override
    public List<IUpload> getUploadsForUser(String username) {
        return uploadRepository.findByUsername(username);
    }
    
    @Override
    public List<IUpload> getUploads() {
        return uploadRepository.findAll();
    }
    
    @Override
    public long getUploadCountForUser(String username) {
        return uploadRepository.countByUsername(username);
    }
    
    @Override
    public long getUploadCount() {
        return uploadRepository.count();
    }
    
    @Override
    public IUpload getUploadsByProgressId(String progressId) {
        return uploadRepository.findByUploadProgressId(progressId);
    }
    
    @Override
    public List<IUpload> getUploadsForUser(String username, int page,
            int pageSize, String sortBy, int sortDirection) {
        String order = sortDirection == ASCENDING ? "ASC" : "DESC";
        return uploadRepository.findByUsernameOrderBy(username, sortBy + " " + order, (page - 1) * pageSize, pageSize);
    }
    
    @Override
    public List<IUpload> getUploads(int page,
            int pageSize, String sortBy, int sortDirection) {
        String order = sortDirection == ASCENDING ? "ASC" : "DESC";
        return uploadRepository.findAllOrderBy(sortBy + " " + order, (page - 1) * pageSize, pageSize); 
    }


    @Override
    protected String getIdPrefix() {
        return "UP";
    }

    @Override
    protected IUpload getById(String id) {
        return getUpload(id);
    }

    @Override
    protected EntityManager getClient() {
       return null;
    }

}
