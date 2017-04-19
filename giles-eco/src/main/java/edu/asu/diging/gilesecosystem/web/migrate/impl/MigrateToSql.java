package edu.asu.diging.gilesecosystem.web.migrate.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IPage;
import edu.asu.diging.gilesecosystem.web.core.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.core.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.impl.File;
import edu.asu.diging.gilesecosystem.web.core.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.impl.Upload;
import edu.asu.diging.gilesecosystem.web.domain.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.domain.DocumentType;
import edu.asu.diging.gilesecosystem.web.domain.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.domain.impl.Page;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalUploadService;

@Service
public class MigrateToSql {

    @PersistenceContext(unitName="DataPU")
    private EntityManager em;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalUploadService uploadService;
    
    @Autowired
    private ITransactionalFileService fileService;
    
    @Autowired
    private ITransactionalProcessingRequestService requestService;
    
    @Async
    public Future<MigrationResult> migrateUserData(String username) throws UnstorableObjectException {
        int docCount = migrateDocuments(username);
        int uploadCount = migrateUploads(username);
        List<String> fileIds = new ArrayList<>();
        int fileCount = migrateFiles(username, fileIds);
        int requestCoun = migrateProcessingRequests(username, fileIds);
        
        return new AsyncResult<MigrationResult>(new MigrationResult(docCount, uploadCount, fileCount, requestCoun, ZonedDateTime.now()));
    }
    
    private int migrateDocuments(String username) throws UnstorableObjectException {
        int counter = 0;
        TypedQuery<Document> query = em.createQuery("SELECT t FROM " + Document.class.getName()  + " t WHERE t.username = '" + username + "'", Document.class);
        List<Document> documents = query.getResultList();
        for (IDocument document : documents) {
            if (documentService.getDocument(document.getId()) == null) {
                documentService.saveDocument(mapDocument(document));
                counter++;
            }
        }
        return counter;
    }
    
    private edu.asu.diging.gilesecosystem.web.domain.IDocument mapDocument(IDocument doc) {
        edu.asu.diging.gilesecosystem.web.domain.IDocument newDoc = new edu.asu.diging.gilesecosystem.web.domain.impl.Document();
        newDoc.setAccess(DocumentAccess.valueOf(doc.getAccess().name()));
        newDoc.setCreatedDate(doc.getCreatedDate());
        newDoc.setDocumentType(DocumentType.valueOf(doc.getDocumentType().name()));
        newDoc.setExtractedTextFileId(doc.getExtractedTextFileId());
        newDoc.setFileIds(doc.getFileIds());
        newDoc.setId(doc.getId());
        newDoc.setPageCount(doc.getPageCount());
        newDoc.setTextFileIds(doc.getTextFileIds());
        newDoc.setUploadedFileId(doc.getUploadedFileId());
        newDoc.setUploadId(doc.getUploadId());
        newDoc.setUsername(doc.getUsername());
        newDoc.setPages(new ArrayList<>());
        for (IPage page : doc.getPages()) {
            edu.asu.diging.gilesecosystem.web.domain.IPage newPage = new Page();
            newPage.setDocument(newDoc);
            newPage.setImageFileId(page.getImageFileId());
            newPage.setOcrFileId(page.getOcrFileId());
            newPage.setPageNr(page.getPageNr());
            newPage.setTextFileId(page.getTextFileId());
            newDoc.getPages().add(newPage);
        }
        return newDoc;
    }
    
    
    private int migrateUploads(String username) throws UnstorableObjectException {
        int counter = 0;
        List<IUpload> uploads = new ArrayList<IUpload>();
        TypedQuery<Upload> query = em.createQuery("SELECT t FROM " + Upload.class.getName()  + " t WHERE t.username = '" + username + "'", Upload.class);
        query.getResultList().forEach(x -> uploads.add(x));
        for (IUpload upload : uploads) {
            if (uploadService.getUpload(upload.getId()) == null) {
                uploadService.saveUpload(mapUpload(upload));
                counter++;
            }
        }
        return counter;
    }
    
    private edu.asu.diging.gilesecosystem.web.domain.IUpload mapUpload(IUpload upload) {
        edu.asu.diging.gilesecosystem.web.domain.IUpload newUp = new edu.asu.diging.gilesecosystem.web.domain.impl.Upload();
        newUp.setCreatedDate(upload.getCreatedDate());
        newUp.setId(upload.getId());
        newUp.setUploadProgressId(upload.getUploadProgressId());
        newUp.setUsername(upload.getUsername());
        return newUp;
    }
    
    private int migrateFiles(String username, List<String> fileIds) throws UnstorableObjectException {
        int counter = 0;
        List<IFile> files = new ArrayList<IFile>();
        TypedQuery<File> query = em.createQuery("SELECT t FROM " + File.class.getName()  + " t WHERE t.username = '" + username + "'", File.class);
        query.getResultList().forEach(x -> files.add(x));
        for (IFile file : files) {
            if (fileService.getFileById(file.getId()) == null) {
                fileService.saveFile(mapFile(file));
                counter++;
            }
            fileIds.add(file.getId());
        }
        return counter;
    }
    
    private edu.asu.diging.gilesecosystem.web.domain.IFile mapFile(IFile file) {
        edu.asu.diging.gilesecosystem.web.domain.IFile newFile = new edu.asu.diging.gilesecosystem.web.domain.impl.File();
        newFile.setAccess(DocumentAccess.valueOf(file.getAccess().name()));
        newFile.setContentType(file.getContentType());
        newFile.setDerivedFrom(file.getDerivedFrom());
        newFile.setDocumentId(file.getDocumentId());
        newFile.setDownloadUrl(file.getDownloadUrl());
        newFile.setFilename(file.getFilename());
        newFile.setFilepath(file.getFilepath());
        newFile.setId(file.getId());
        newFile.setRequestId(file.getRequestId());
        newFile.setSize(file.getSize());
        newFile.setStorageId(file.getStorageId());
        newFile.setUploadDate(file.getUploadDate());
        newFile.setUploadId(file.getUploadId());
        newFile.setUsername(file.getUsername());
        newFile.setUsernameForStorage(file.getUsernameForStorage());
        newFile.setProcessingStatus(ProcessingStatus.valueOf(file.getProcessingStatus().name()));
        return newFile;
    }
    
    private int migrateProcessingRequests(String username, List<String> fileIds) throws UnstorableObjectException {
        int counter = 0;
        List<IProcessingRequest> requests = new ArrayList<IProcessingRequest>();
        TypedQuery<ProcessingRequest> query = em.createQuery("SELECT t FROM " + ProcessingRequest.class.getName()  + " t", ProcessingRequest.class);
        query.getResultList().forEach(x -> requests.add(x));
        for (IProcessingRequest request : requests) {
            if (fileIds.contains(request.getFileId())) {
                requestService.save(mapRequest(request));
                counter++;
            }
        }
        return counter;
    }
    
    private edu.asu.diging.gilesecosystem.web.domain.IProcessingRequest mapRequest(IProcessingRequest request) {
        edu.asu.diging.gilesecosystem.web.domain.IProcessingRequest newReq = new edu.asu.diging.gilesecosystem.web.domain.impl.ProcessingRequest();
        newReq.setCompletedRequest(request.getCompletedRequest());
        newReq.setDocumentId(request.getDocumentId());
        newReq.setFileId(request.getFileId());
        newReq.setId(request.getId());
        newReq.setRequestId(request.getRequestId());
        if (request.getRequestStatus() != null) {
            newReq.setRequestStatus(RequestStatus.valueOf(request.getRequestStatus().name()));
        }
        newReq.setSentRequest(request.getSentRequest());
        return newReq;
    }
}
