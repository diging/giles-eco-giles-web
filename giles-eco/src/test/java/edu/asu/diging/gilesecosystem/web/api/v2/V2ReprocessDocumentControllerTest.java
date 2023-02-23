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

import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.reprocessing.IReprocessingService;

public class V2ReprocessDocumentControllerTest {
    
    @Mock
    private IReprocessingService reprocessingService;
    
    @Mock
    private ITransactionalDocumentService documentService;
    
    @InjectMocks
    private V2ReprocessDocumentController v2ReprocessDocumentController;
    
    private String DOCUMENT_ID = "documentId";
    private DocumentAccess access = DocumentAccess.PRIVATE;
    private String FILE_ID = "fileId";
    private String UPLOAD_ID = "uploadId";
    
    IDocument document;
    
    @Before
    public void setUp() {
        document = createDocument();
        v2ReprocessDocumentController = new V2ReprocessDocumentController();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void test_reprocessDocument_success() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(document);
        ResponseEntity<String> response = v2ReprocessDocumentController.reprocessDocument(DOCUMENT_ID);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void test_reprocessDocument_notFound() {
        Mockito.when(documentService.getDocument(DOCUMENT_ID)).thenReturn(null);
        ResponseEntity<String> response = v2ReprocessDocumentController.reprocessDocument(DOCUMENT_ID);
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
