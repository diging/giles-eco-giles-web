package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;

@Component
public class FilesDatabaseClient extends DatabaseClient<IFile> implements
        IFilesDatabaseClient {

    @PersistenceContext(unitName="entityManagerFactory")
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
        IFile existingFile = getById(file.getId());
        if (existingFile == null) {
            return store(file);
        }
        
        return update(file);
    }

    @Override
    public IFile getFileById(String id) {
        return em.find(File.class, id);
    }
    
    @Override
    public List<IFile> getFilesForIds(List<String> ids) {
        TypedQuery<IFile> query = getClient().createQuery("SELECT t FROM " + File.class.getName()  + " t WHERE t.id IN (:ids)", IFile.class);
        query.setParameter("ids", ids);
        return query.getResultList();
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
    
    @Override
    public List<IFile> getFilesByDerivedFrom(String derivedFromId) {
        return searchByProperty("derivedFrom", derivedFromId, File.class);
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
    
    @Override
    public void deleteFiles(String documentId) {
        CriteriaBuilder builder = getClient().getCriteriaBuilder();
        CriteriaQuery<File> query = builder.createQuery(File.class);
        Root<File> root = query.from(File.class);
        query.where(builder.equal(root.get("documentId"), documentId));
        
        // Fetch the list of files to be deleted
        List<File> filesToDelete = getClient().createQuery(query).getResultList();

        for (File file : filesToDelete) {
            file.getOldFileVersionIds().clear();
            getClient().remove(file);
        }
    }
}
