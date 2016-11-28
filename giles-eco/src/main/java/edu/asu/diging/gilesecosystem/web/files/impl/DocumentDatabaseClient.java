package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.impl.Document;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.IPropertiesCopier;

@Transactional("txmanager_data")
@Component
public class DocumentDatabaseClient extends DatabaseClient<IDocument> implements
        IDocumentDatabaseClient {

    @PersistenceContext(unitName="DataPU")
    private EntityManager em;
    
    @Autowired
    private IPropertiesCopier copier;


    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#addFile(edu.asu.giles
     * .core.IDocument)
     */
    @Override
    public IDocument saveDocument(IDocument document) throws UnstorableObjectException {
        IDocument existing = getById(document.getId());
        
        if (existing == null) {
            return store(document);
        }
        
        copier.copyObject(document, existing);
        return document;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#getFileById(java.lang
     * .String)
     */
    @Override
    public IDocument getDocumentById(String id) {
        return em.find(Document.class, id);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#getFileByUploadId(java
     * .lang.String)
     */
    @Override
    public List<IDocument> getDocumentByUploadId(String uploadId) {
        return getDocumentList("uploadId", uploadId);
    }
    
    @Override
    public List<IDocument> getDocumentsByUsername(String username) {
        return getDocumentList("username", username);
    }
    
    protected List<IDocument> getDocumentList(String propName, String propValue) {
        List<IDocument> docs = new ArrayList<IDocument>();
        searchByProperty(propName, propValue, Document.class).forEach(x -> docs.add(x));
        return docs;
    }

    @Override
    protected String getIdPrefix() {
        return "DOC";
    }

    @Override
    protected IDocument getById(String id) {
        return getDocumentById(id);
    }

    @Override
    protected EntityManager getClient() {
        return em;
    }
}
