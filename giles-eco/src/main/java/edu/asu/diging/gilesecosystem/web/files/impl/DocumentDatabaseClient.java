package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.impl.Document;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;

@Transactional("txmanager_documents")
@Component
public class DocumentDatabaseClient extends DatabaseClient<IDocument> implements
        IDocumentDatabaseClient {

    @PersistenceContext(unitName="DocumentsPU")
    private EntityManager em;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#addFile(edu.asu.giles
     * .core.IDocument)
     */
    @Override
    public IDocument saveDocument(IDocument document) throws UnstorableObjectException {
        if (getById(document.getId()) == null) {
            return store(document);
        }
        IDocument existing = getById(document.getId());
        existing.setAccess(document.getAccess());
        existing.setCreatedDate(document.getCreatedDate());
        existing.setDocumentType(document.getDocumentType());
        existing.setExtractedTextFileId(document.getExtractedTextFileId());
        existing.setFileIds(document.getFileIds());
        existing.setDocumentId(document.getDocumentId());
        existing.setPageCount(document.getPageCount());
        existing.setPages(document.getPages());
        existing.setRequest(document.getRequest());
        existing.setTextFileIds(document.getTextFileIds());
        existing.setUploadedFileId(document.getUploadedFileId());
        existing.setUploadId(document.getUploadId());
        existing.setUsername(document.getUsername());
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
