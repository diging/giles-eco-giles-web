package edu.asu.diging.gilesecosystem.web.service.reprocessing.impl;

import static org.mockito.Mockito.times;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;
import edu.asu.diging.gilesecosystem.web.core.repository.ProcessingRequestRepository;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.impl.StorageRequestProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.impl.ReprocessingService;
import edu.asu.diging.gilesecosystem.web.core.users.AccountStatus;
import edu.asu.diging.gilesecosystem.web.core.users.IUserManager;
import edu.asu.diging.gilesecosystem.web.core.users.User;

public class ReprocessingServiceTest {
    @Mock
    private ITransactionalFileService fileService;
    
    @Mock
    private IFilesManager filesManager;
    
    @Mock
    private IProcessingCoordinator processCoordinator;
    
    @Mock
    private IUserManager userManager;
    
    @Mock
    private ISystemMessageHandler messageHandler;
    
    @Mock
    private IProcessingRequestsDatabaseClient procReqDbClient;
    
    @Mock
    private ProcessingRequestRepository processingRequestRepository;
    
    @Mock
    private IUploadDatabaseClient uploadDatabaseClient;
    
    @InjectMocks
    private IReprocessingService reprocessingService;
    
    private String FILE_ID = "fileId";
    private String CONTENT_TYPE = "contentType";
    private String DOCUMENT_ID = "documentId";
    private String FILENAME = "filename";
    private long SIZE = 1000;
    private String UPLOAD_DATE = "date";
    private String UPLOAD_ID = "uploadId";
    private String PROCESSING_REQUEST_ID = "processingRequestId";
    private DocumentAccess access = DocumentAccess.PRIVATE;
    private IDocument document;
    private IFile file;
    private IProcessingRequest request;
    private User user;
    private IUpload upload;
    
    @Before
    public void setUp() {
        reprocessingService = new ReprocessingService();
        MockitoAnnotations.initMocks(this);
        user = createUser();
        upload = createUpload();
        document = createDocument();
        file = createFile();
        request = createProcessingRequest();
        Mockito.when(fileService.getFileById(FILE_ID)).thenReturn(file);
        Mockito.when(filesManager.getFilesOfDocument(document)).thenReturn(Arrays.asList(file));
        Mockito.when(procReqDbClient.getRequestByDocumentId(document.getId())).thenReturn(Arrays.asList(request));
        Mockito.when(userManager.findUser(file.getUsername())).thenReturn(user);
        Mockito.when(uploadDatabaseClient.getUpload(document.getUploadId())).thenReturn(upload);
    }
    
    @Test
    public void test_reprocessDocument_success() {
        reprocessingService.reprocessDocument(document);
        Mockito.verify(filesManager, times(1)).changeFileProcessingStatus(file, ProcessingStatus.UNPROCESSED);
        Mockito.verify(processingRequestRepository, times(1)).deleteById(PROCESSING_REQUEST_ID);
    }
    
    private Document createDocument() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setAccess(access);
        document.setFileIds(Arrays.asList(FILE_ID));
        document.setUploadId(UPLOAD_ID);
        document.setUploadedFileId(FILE_ID);
        return document;
    }
    
    private File createFile() {
        File file = new File();
        file.setId(FILE_ID);
        file.setAccess(access);
        file.setContentType(CONTENT_TYPE);
        file.setDocumentId(DOCUMENT_ID);
        file.setFilename(FILENAME);
        file.setSize(SIZE);
        file.setUploadDate(UPLOAD_DATE);
        file.setUploadId(UPLOAD_ID);
        file.setProcessingStatus(ProcessingStatus.FAILED);
        file.setUsername(user.getName());
        return file;
    }
    
    private ProcessingRequest createProcessingRequest() {
        ProcessingRequest processingRequest = new ProcessingRequest();
        processingRequest.setDocumentId(DOCUMENT_ID);
        processingRequest.setFileId(FILE_ID);
        processingRequest.setId(PROCESSING_REQUEST_ID);
        processingRequest.setRequestStatus(RequestStatus.FAILED);
        return processingRequest;
    }
    
    private User createUser() {
        User user = new User();
        user.setAccountStatus(AccountStatus.APPROVED);
        user.setEmail("test@asu.edu");
        user.setName("Test User");
        user.setProvider("github");
        user.setUserIdOfProvider("37469232");
        return user;
    }
    
    private Upload createUpload() {
        Upload upload = new Upload();
        upload.setId(UPLOAD_ID);
        upload.setCreatedDate(UPLOAD_DATE);
        return upload;
    }
}
