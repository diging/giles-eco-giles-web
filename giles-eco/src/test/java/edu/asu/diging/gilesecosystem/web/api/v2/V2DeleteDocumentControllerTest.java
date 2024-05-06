package edu.asu.diging.gilesecosystem.web.api.v2;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.api.util.IResponseHelper;
import edu.asu.diging.gilesecosystem.web.config.CitesphereToken;
import edu.asu.diging.gilesecosystem.web.core.citesphere.ICitesphereConnector;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalProcessingRequestService;
import edu.asu.diging.gilesecosystem.web.core.service.delete.IDeleteDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.core.users.CitesphereUser;

public class V2DeleteDocumentControllerTest {
    
    @Mock
    private IDeleteDocumentService deleteDocumentService;
    
    @Mock
    private ITransactionalDocumentService documentService;
    
    @Mock
    private IPropertiesManager propertyManager;
    
    @Mock
    private IResponseHelper responseHelper;
    
    @Mock
    private ICitesphereConnector citesphereConnector;
    
    @Mock
    private ITransactionalProcessingRequestService processingRequestService;
    
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
        Mockito.when(propertyManager.getProperty(Properties.GILES_URL)).thenReturn("http://localhost:8085/giles/");
        Mockito.when(citesphereConnector.hasAccess(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        CITESPHERE_TOKEN.setPrincipal(new CitesphereUser("testUser", "oauth", new ArrayList<>()));
    }
    
    @Test
    public void test_deleteDocument_success() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("checkUrl", "http://localhost:8085/giles/api/v2/files/deletion/check/" + DOCUMENT_ID);
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(msgs, HttpStatus.OK));
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getBody().contains("http://localhost:8085/giles/api/v2/files/deletion/check/documentId"));
    }
    
    @Test
    public void test_deleteDocument_whenUserIsNotAuthorized() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Mockito.when(citesphereConnector.hasAccess(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        Map<String, String> unauthorizedMsgs = new HashMap<String, String>();
        unauthorizedMsgs.put("errorMsg", "User is not authorized to delete the document with id " + document.getId());
        unauthorizedMsgs.put("errorCode", "401");      
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(unauthorizedMsgs, HttpStatus.FORBIDDEN));
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    public void test_deleteDocument_notFound() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(null);
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("errorMsg", "Document with id: " + DOCUMENT_ID + " does not exist.");
        msgs.put("errorCode", "404");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(msgs, HttpStatus.NOT_FOUND));
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    public void test_checkDocumentDeletion_whenDocumentIsDeleted() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(null);
        Map<String, String> successMsgs = new HashMap<String, String>();
        successMsgs.put("successMessage", "Document Id: " + DOCUMENT_ID + " is deleted.");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(successMsgs, HttpStatus.OK));
        ResponseEntity<String> response = v2DeleteDocumentController.checkDocumentDeletion(null, DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void test_checkDocumentDeletion_whenDocumentIsNotDeleted() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("progressInfo", "Deletion in progress. Please check back later.");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(msgs, HttpStatus.OK));
        ResponseEntity<String> response = v2DeleteDocumentController.checkDocumentDeletion(null, DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void test_checkDocumentDeletion_whenUserDoesNotHavePermissionToCheckDocumentStatus() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Mockito.when(citesphereConnector.hasAccess(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        Map<String, String> unauthorizedMsgs = new HashMap<String, String>();
        unauthorizedMsgs.put("errorMsg", "User is not authorized to check status for document id " + document.getId());
        unauthorizedMsgs.put("errorCode", "401");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(unauthorizedMsgs, HttpStatus.FORBIDDEN));
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    public void test_checkDocumentDeletion_whenDeletionFailed() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        Map<String, String> msgs = new HashMap<String, String>();
        msgs.put("progressInfo", "Deletion in progress. Please check back later.");
        Mockito.when(responseHelper.generateResponse(Mockito.anyMap(), Mockito.any(HttpStatus.class))).thenReturn(generateResponse(msgs, HttpStatus.INTERNAL_SERVER_ERROR));
        IProcessingRequest procRequest = new ProcessingRequest();
        procRequest.setRequestStatus(RequestStatus.FAILED);
        List<IProcessingRequest> procList = new ArrayList<IProcessingRequest>();
        procList.add(procRequest);
        Mockito.when(processingRequestService.getProcRequestsByRequestId(Mockito.anyString())).thenReturn(procList);
        ResponseEntity<String> response = v2DeleteDocumentController.deleteDocument(DOCUMENT_ID, CITESPHERE_TOKEN);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
    
    private Document createDocument() {
        Document document = new Document();
        document.setId(DOCUMENT_ID);
        document.setAccess(access);
        document.setFileIds(Arrays.asList(FILE_ID));
        document.setUploadId(UPLOAD_ID);
        document.setUploadedFileId(FILE_ID);
        document.setRequestId("REQ123");
        return document;
    }
    
    private ResponseEntity<String> generateResponse(Map<String, String> msgs,
            HttpStatus status) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode root = mapper.createObjectNode();
        for (String key : msgs.keySet()) {
            root.put(key, msgs.get(key));
        }

        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, root);
        } catch (IOException e) {
            return new ResponseEntity<String>(
                    "{\"errorMsg\": \"Could not write json result.\", \"errorCode\": \"errorCode\": \"500\" }",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(sw.toString(), status);
    }
}
