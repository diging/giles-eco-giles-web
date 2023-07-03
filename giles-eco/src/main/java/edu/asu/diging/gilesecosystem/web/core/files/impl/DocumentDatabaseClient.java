package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.asu.diging.gilesecosystem.util.store.objectdb.DatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.repository.DocumentRepository;

@Component
public class DocumentDatabaseClient extends DatabaseClient<IDocument> implements
        IDocumentDatabaseClient {

    private final DocumentRepository documentRepository;

    @Autowired
    public DocumentDatabaseClient(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.files.impl.IDocumentDatabaeClient#addFile(edu.asu.giles
     * .core.IDocument)
     */
    @Override
    public IDocument saveDocument(IDocument document) throws IllegalArgumentException {
        return documentRepository.save((Document) document);
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
        return documentRepository.findById(id).orElse(null);
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
        return documentRepository.findByUploadId(uploadId);
    }
    
    @Override
    public List<IDocument> getDocumentsByUsername(String username) {
        return documentRepository.findByUsername(username);
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
        return null;
    }
}
