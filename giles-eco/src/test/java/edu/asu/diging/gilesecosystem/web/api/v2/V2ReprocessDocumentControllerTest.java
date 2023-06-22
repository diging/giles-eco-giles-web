package edu.asu.diging.gilesecosystem.web.api.v2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;

public class V2ReprocessDocumentControllerTest {
    
    @Mock
    private IReprocessingService reprocessingService;
    
    @Mock
    private ITransactionalDocumentService documentService;
    
    @Mock
    private IPropertiesManager propertyManager;
    
    @Mock
    private IResponseHelper responseHelper;
    
    @Mock
    private ITransactionalUploadService uploadService;
    
    @Mock
    private IUserHelper userHelper;
    
    @InjectMocks
    private V2ReprocessDocumentController v2ReprocessDocumentController;
    
    private String DOCUMENT_ID = "documentId";
    private DocumentAccess access = DocumentAccess.PRIVATE;
    private String FILE_ID = "fileId";
    private String UPLOAD_ID = "uploadId";
    private String UPLOAD_PROGRESS_ID = "uploadProgressId";
    private CitesphereToken citesphereToken = new CitesphereToken("71b9cc36-939d-4e28-89e9-e5bfca1b26c3");
    
    private IDocument document;
    
    @Before
    public void setUp() {
        document = createDocument();
        v2ReprocessDocumentController = new V2ReprocessDocumentController();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_reprocessDocument_success() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Mockito.when(propertyManager.getProperty(Properties.GILES_URL)).thenReturn("http://localhost:8085/giles");
        Mockito.when(userHelper.isUserPermittedToAccessDocument(document, citesphereToken)).thenReturn(true);
        Mockito.when(uploadService.getUpload(Mockito.anyString())).thenReturn(createUpload());
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("id", DOCUMENT_ID);
        msgs.put("checkUrl", propertyManager.getProperty(Properties.GILES_URL) + "/api/v2/files/upload/check/" + UPLOAD_ID);
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(new ResponseEntity<String>(msgs.toString(), HttpStatus.OK));
        ResponseEntity<String> response = v2ReprocessDocumentController.reprocessDocument(DOCUMENT_ID, citesphereToken);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(msgs.toString(), response.getBody());
    }
    
    @Test
    public void test_reprocessDocument_whenUserDoesNotHavePermission() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Mockito.when(userHelper.isUserPermittedToAccessDocument(document, citesphereToken)).thenReturn(false);
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("errorMsg", "User is not authorized to reprocess the document.");
        msgs.put("errorCode", "401");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(new ResponseEntity<String>(msgs.toString(), HttpStatus.UNAUTHORIZED));
        ResponseEntity<String> response = v2ReprocessDocumentController.reprocessDocument(DOCUMENT_ID, citesphereToken);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assert.assertEquals(msgs.toString(), response.getBody());
    }
    
    @Test
    public void test_reprocessDocument_notFound() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(null);
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("errorMsg", "Document with id: " + DOCUMENT_ID + " does not exist.");
        msgs.put("errorCode", "404");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(new ResponseEntity<String>(msgs.toString(), HttpStatus.NOT_FOUND));
        ResponseEntity<String> response = v2ReprocessDocumentController.reprocessDocument(DOCUMENT_ID, citesphereToken);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertEquals(msgs.toString(), response.getBody());
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
    
    private Upload createUpload() {
        Upload upload = new Upload();
        upload.setId(UPLOAD_ID);
        upload.setUploadProgressId(UPLOAD_PROGRESS_ID);
        return upload;
    }
}
