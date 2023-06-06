package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;

@Transactional("transactionManager")
@Service
public class UploadDatabaseClient extends DatabaseClient<IUpload> implements
        IUploadDatabaseClient {

    @PersistenceContext(unitName="entityManagerFactory")
    private EntityManager em;
    
    @Override
    public IUpload saveUpload(IUpload upload) throws UnstorableObjectException {
        IUpload existing = getById(upload.getId());
        
        if (existing == null) {
            return store(upload);
        }
        
        return update(upload);
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
        return em.find(Upload.class, id);
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
        List<IUpload> uploads = new ArrayList<IUpload>();
        searchByProperty("username", username, Upload.class).forEach(x -> uploads.add(x));
        
        return uploads;
    }
    
    @Override
    public List<IUpload> getUploads() {
        List<IUpload> results = new ArrayList<IUpload>();
        TypedQuery<Upload> query = getClient().createQuery("SELECT t FROM Upload t", Upload.class);
        query.getResultList().forEach(x -> results.add(x));
        return results;
    }
    
    @Override
    public long getUploadCountForUser(String username) {
        TypedQuery<Long> query = getClient().createQuery("SELECT count(t) FROM Upload t WHERE t.username = '" + username + "'", Long.class);
        return query.getSingleResult();
    }
    
    @Override
    public long getUploadCount() {
        TypedQuery<Long> query = getClient().createQuery("SELECT count(t) FROM Upload t", Long.class);
        return query.getSingleResult();
    }
    
    @Override
    public IUpload getUploadsByProgressId(String progressId) {
        List<IUpload> uploads = new ArrayList<IUpload>();
        searchByProperty("uploadProgressId", progressId, Upload.class).forEach(x -> uploads.add(x));
        
        // we assume there is just one
        if (!uploads.isEmpty()) {
            return uploads.get(0);
        }
        
        return null;
    }
    
    @Override
    public List<IUpload> getUploadsForUser(String username, int page,
            int pageSize, String sortBy, int sortDirection) {
        
        String order = "DESC";
        if (sortDirection == IUploadDatabaseClient.ASCENDING) {
            order = "ASC";
        }
        
        TypedQuery<IUpload> query = em.createQuery("SELECT u FROM Upload u WHERE u.username=:username ORDER BY u." + sortBy + " " + order, IUpload.class);
        query.setParameter("username", username).setFirstResult((page-1)*pageSize).setMaxResults(pageSize);

        return query.getResultList(); 
    }
    
    @Override
    public List<IUpload> getUploads(int page,
            int pageSize, String sortBy, int sortDirection) {
        
        String order = "DESC";
        if (sortDirection == IUploadDatabaseClient.ASCENDING) {
            order = "ASC";
        }
        
        TypedQuery<IUpload> query = em.createQuery("SELECT u FROM Upload u ORDER BY u." + sortBy + " " + order, IUpload.class);
        query.setFirstResult((page-1)*pageSize).setMaxResults(pageSize);

        return query.getResultList(); 
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
       return em;
    }
}
