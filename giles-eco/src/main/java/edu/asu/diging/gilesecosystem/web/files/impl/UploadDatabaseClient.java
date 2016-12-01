package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.core.impl.Upload;
import edu.asu.diging.gilesecosystem.web.files.IUploadDatabaseClient;

@Transactional("txmanager_data")
@Service
public class UploadDatabaseClient extends DatabaseClient<IUpload> implements
        IUploadDatabaseClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext(unitName="DataPU")
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
