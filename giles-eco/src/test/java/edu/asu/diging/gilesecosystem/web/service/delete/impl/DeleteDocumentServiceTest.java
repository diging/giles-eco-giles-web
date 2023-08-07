package edu.asu.diging.gilesecosystem.web.service.delete.impl;

import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.exceptions.MessageCreationException;
import edu.asu.diging.gilesecosystem.requests.impl.StorageDeletionRequest;
import edu.asu.diging.gilesecosystem.requests.kafka.IRequestProducer;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.impl.DeleteDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;

public class DeleteDocumentServiceTest {
    @Mock
    private ITransactionalFileService fileService;
    
    @Mock
    private ITransactionalUploadService uploadService;
    
    @Mock
    private ITransactionalDocumentService documentService;

    @Mock
    private IFilesManager filesManager;
    
    @Mock
    private ISystemMessageHandler messageHandler;
    
    @Mock 
    private ApplicationContext ctx;
    
    @Mock
    private IRequestProducer requestProducer;
    
    @Mock
    private IPropertiesManager propertyManager;
    
    @Mock
    private IRequestFactory<IStorageDeletionRequest, StorageDeletionRequest> requestFactory;
    
    @Mock
    private ITransactionalProcessingRequestService processingRequestService;
    
    @Mock
    private ITransactionalRequestService requestService; 
    
    @InjectMocks
    private IDeleteDocumentService factoryToTest;
    
    IDocument document;
    IFile file1, file2;
    IUpload upload;
    List<IFile> files;
    IStorageDeletionRequest storageDeletionRequest1;
    
    private String FILE_ID_1 = "fileId1";
    private String FILE_ID_2 = "fileId2";
    private String CONTENT_TYPE = "contentType";
    private String DOCUMENT_ID = "documentId";
    private String FILENAME = "filename";
    private long SIZE = 1000;
    private String UPLOAD_DATE = "date";
    private String UPLOAD_ID = "uploadId";
    private String STORAGE_ID_1 = "FILEId1";
    private String STORAGE_ID_2 = "FILEId2";
    private String REQUEST_ID_1 = "REQ1";
    private String REQUEST_ID_2 = "REQ2";
    
    @Before
    public void setUp() throws InstantiationException, IllegalAccessException{
        factoryToTest = new DeleteDocumentService();
        upload = createUpload();
        document = createDocument();
        file1 = createFile(FILE_ID_1, STORAGE_ID_1, REQUEST_ID_1);
        file2 = createFile(FILE_ID_2, STORAGE_ID_2, REQUEST_ID_2);
        files = new ArrayList<IFile>();
        files.add(file1);
        files.add(file2);
        storageDeletionRequest1 = createStorageDeletionRequest(REQUEST_ID_1, UPLOAD_ID);
        MockitoAnnotations.initMocks(this);
        Mockito.when(propertyManager.getProperty(Properties.KAFKA_TOPIC_STORAGE_DELETION_REQUEST)).thenReturn("request_storage_deletion_topic");
        Mockito.when(requestFactory.createRequest(Mockito.anyString(), Mockito.anyString())).thenReturn(storageDeletionRequest1);
    }
    
    @Test
    public void test_deleteDocument_success() throws MessageCreationException {
        factoryToTest.deleteDocument(document);
        Mockito.verify(requestProducer, times(1)).sendRequest(storageDeletionRequest1, "request_storage_deletion_topic");
    }
    
    @Test
    public void test_deleteDocumentAfterStorageDeletion_success() {
        factoryToTest.deleteDocumentAfterStorageDeletion(document);
        Mockito.verify(documentService, times(1)).deleteDocument(document.getId());
        Mockito.verify(uploadService, times(1)).deleteUpload(document.getUploadId());
    }
    
    @Test
    public void test_deleteDocumentAfterStorageDeletion_withAnUploadHavingMultipleDocuments_success() {
        List<IDocument> docs = new ArrayList<IDocument>();
        docs.add(document);
        Mockito.when(documentService.getDocumentsByUploadId(document.getUploadId())).thenReturn(docs);
        factoryToTest.deleteDocumentAfterStorageDeletion(document);
        Mockito.verify(documentService, times(1)).deleteDocument(document.getId());
        Mockito.verify(uploadService, times(0)).deleteUpload(document.getUploadId());
    }
    
    private Upload createUpload() {
        Upload upload = new Upload();
        upload.setId(UPLOAD_ID);
        upload.setCreatedDate(UPLOAD_DATE);
        return upload;
    }
    
    private Document createDocument() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setAccess(DocumentAccess.PRIVATE);
        document.setFileIds(Arrays.asList(FILE_ID_1, FILE_ID_2));
        document.setUploadId(UPLOAD_ID);
        document.setUploadedFileId(FILE_ID_1);
        return document;
    }
    
    private File createFile(String fileId, String storageId, String reqId) {
        File file = new File();
        file.setId(fileId);
        file.setAccess(DocumentAccess.PRIVATE);
        file.setContentType(CONTENT_TYPE);
        file.setDocumentId(DOCUMENT_ID);
        file.setFilename(FILENAME);
        file.setSize(SIZE);
        file.setUploadDate(UPLOAD_DATE);
        file.setUploadId(UPLOAD_ID);
        file.setProcessingStatus(ProcessingStatus.COMPLETE);
        file.setUsername("github_3123");
        file.setStorageId(storageId);
        file.setRequestId(reqId);
        return file;
    }
    
    private StorageDeletionRequest createStorageDeletionRequest(String requestId, String uploadId) {
        StorageDeletionRequest storageDeletionRequest = new StorageDeletionRequest();
        storageDeletionRequest.setRequestId(requestId);
        storageDeletionRequest.setUploadId(uploadId);
        return storageDeletionRequest;
    }
}
