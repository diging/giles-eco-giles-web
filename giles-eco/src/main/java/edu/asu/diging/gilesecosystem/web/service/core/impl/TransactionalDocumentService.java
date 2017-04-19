package edu.asu.diging.gilesecosystem.web.service.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.domain.DocumentType;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.impl.Document;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;

@Service
@Transactional("transactionManager")
public class TransactionalDocumentService implements ITransactionalDocumentService {

    @Autowired
    private IDocumentDatabaseClient documentDatabaseClient;

    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.core.impl.ITransactionalDocumentService#saveDocument(edu.asu.diging.gilesecosystem.web.core.IDocument)
     */
    @Override
    public void saveDocument(IDocument document) throws UnstorableObjectException {
        documentDatabaseClient.saveDocument(document);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.core.impl.ITransactionalDocumentService#generateDocumentId()
     */
    @Override
    public String generateDocumentId() {
        return documentDatabaseClient.generateId();
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.core.impl.ITransactionalDocumentService#getDocumentsByUploadId(java.lang.String)
     */
    @Override
    public List<IDocument> getDocumentsByUploadId(String uploadId) {
        return documentDatabaseClient.getDocumentByUploadId(uploadId);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.core.impl.ITransactionalDocumentService#getDocumentsByUsername(java.lang.String)
     */
    @Override
    public List<IDocument> getDocumentsByUsername(String username) {
        return documentDatabaseClient.getDocumentsByUsername(username);
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.gilesecosystem.web.service.core.impl.ITransactionalDocumentService#getDocument(java.lang.String)
     */
    @Override
    public IDocument getDocument(String id) {
        return documentDatabaseClient.getDocumentById(id);
    }
    
    @Override
    public IDocument createDocument(String uploadId, String uploadDate,
            DocumentAccess access, DocumentType docType, String username) {
        IDocument document = new Document();
        String docId = generateDocumentId();
        document.setUsername(username);
        document.setId(docId);
        document.setCreatedDate(uploadDate);
        document.setAccess(access);
        document.setUploadId(uploadId);
        document.setFileIds(new ArrayList<>());
        document.setTextFileIds(new ArrayList<>());
        document.setDocumentType(docType);

        return document;
    }
}
