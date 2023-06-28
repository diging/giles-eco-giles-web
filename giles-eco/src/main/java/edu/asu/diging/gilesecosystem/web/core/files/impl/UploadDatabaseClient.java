package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;
import edu.asu.diging.gilesecosystem.web.core.repository.UploadRepository;

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
        return uploadRepository.save((Upload) upload);
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
        return uploadRepository.findAll().stream().map(upload -> (IUpload) upload).collect(Collectors.toList());
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
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortBy));
        List<Upload> uploads =  uploadRepository.findByUsername(username, pageable);
        return uploads.stream()
                .map(upload -> (IUpload) upload)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<IUpload> getUploads(int page,
            int pageSize, String sortBy, int sortDirection) {
        Sort.Direction direction = sortDirection == ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(direction, sortBy));
        List<Upload> uploads = uploadRepository.findAllOrderBy(sortBy, pageable);
        return uploads.stream()
                .map(upload -> (IUpload) upload)
                .collect(Collectors.toList());
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
