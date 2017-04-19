package edu.asu.diging.gilesecosystem.web.service.core;

import java.util.ArrayList;
import java.util.List;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.domain.DocumentType;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.impl.Document;

public interface ITransactionalDocumentService {

    void saveDocument(IDocument document) throws UnstorableObjectException;

    String generateDocumentId();

    List<IDocument> getDocumentsByUploadId(String uploadId);

    List<IDocument> getDocumentsByUsername(String username);

    IDocument getDocument(String id);

    IDocument createDocument(String uploadId, String uploadDate, DocumentAccess access, DocumentType docType,
            String username);

}