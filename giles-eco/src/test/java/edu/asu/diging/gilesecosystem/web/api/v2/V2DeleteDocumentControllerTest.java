package edu.asu.diging.gilesecosystem.web.api.v2;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.api.util.IResponseHelper;
import edu.asu.diging.gilesecosystem.web.config.CitesphereToken;
import edu.asu.diging.gilesecosystem.web.config.IUserHelper;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;

public class V2DeleteDocumentControllerTest {
    
    @Mock
    private IDeleteDocumentService deleteDocumentService;
    
    @Mock
    private ITransactionalDocumentService documentService;
    
    @Mock
    private IUserHelper userHelper;
    
    @Mock
    private IPropertiesManager propertyManager;
    
    @Mock
    private IResponseHelper responseHelper;
    
    @InjectMocks
    private V2DeleteDocumentController v2DeleteDocumentController;
    
    private String DOCUMENT_ID = "documentId";
    private DocumentAccess access = DocumentAccess.PRIVATE;
    private String FILE_ID = "fileId";
    private String UPLOAD_ID = "uploadId";
    private CitesphereToken CITESPHERE_TOKEN = new CitesphereToken("71b9cc36-939d-4e28-89e9-e5bfca1b26c3");
    IDocument document;
    
    @Before
    public void setUp() {
        document = createDocument();
        v2DeleteDocumentController = new V2DeleteDocumentController();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_deleteDocument_success() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Mockito.when(userHelper.checkUserPermission(document, CITESPHERE_TOKEN)).thenReturn(true);
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void test_deleteDocument_whenUserIsNotAuthorized() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Mockito.when(userHelper.checkUserPermission(document, CITESPHERE_TOKEN)).thenReturn(false);
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    public void test_deleteDocument_notFound() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(null);
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
}
