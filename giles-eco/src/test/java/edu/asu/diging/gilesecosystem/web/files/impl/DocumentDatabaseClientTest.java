package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.store.IPropertiesCopier;
import edu.asu.diging.gilesecosystem.web.core.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.impl.DocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;

public class DocumentDatabaseClientTest {
    
    Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Mock private EntityManager em;
    
    @Mock private IPropertiesCopier copier;
     
    @InjectMocks @Spy private IDocumentDatabaseClient docDatabaseClientToTest;
    
    private final String DOC1_ID = "DOC1";
    private final String DOC2_ID = "DOC1";
    private final String DOC_ID_NOT_EXIST = "DOC_NOT_EXISTS";
    private final String UPLOAD1_ID = "UP1";
    private final String USERNAME = "testuser";
    private final String REQUEST_ID = "DELREQ123";
    
    @Before
    public void setUp() {
        docDatabaseClientToTest = new DocumentDatabaseClient();
        MockitoAnnotations.initMocks(this);
        
        IDocument doc1 = new Document();
        doc1.setId(DOC1_ID);
        doc1.setUploadId(UPLOAD1_ID);
        
        Mockito.when(em.find(Document.class, DOC1_ID)).thenReturn((Document) doc1);
        
    }
    
    @Test
    public void test_saveDocument_new() throws UnstorableObjectException {
        IDocument doc = new Document();
        doc.setId("id");
        docDatabaseClientToTest.saveDocument(doc);
        Mockito.verify(em).persist(doc);
        Mockito.verify(em).flush();;
    }
    
    @Test
    public void test_saveDocument_existingDoc() throws UnstorableObjectException {
        IDocument doc = new Document();
        doc.setId(DOC1_ID);
        docDatabaseClientToTest.saveDocument(doc);
        Mockito.verify(docDatabaseClientToTest).update(doc);
    }
    
    @Test(expected = UnstorableObjectException.class)
    public void test_saveDocument_noId() throws UnstorableObjectException {
        IDocument doc = new Document();
        docDatabaseClientToTest.saveDocument(doc);
    }
    
    @Test
    public void test_getDocumentById_docExists() {
        IDocument doc = docDatabaseClientToTest.getDocumentById(DOC1_ID);
        Assert.assertEquals(DOC1_ID, doc.getId());
    }
    
    @Test
    public void test_getDocumentById_docNotExists() {
        Mockito.when(em.find(Document.class, DOC_ID_NOT_EXIST)).thenReturn(null);
        IDocument doc = docDatabaseClientToTest.getDocumentById(DOC_ID_NOT_EXIST);
        Assert.assertNull(doc);
    }
    
    @Test
    public void test_getDocumentByUploadId_docExists() throws Exception {
        IDocument doc1 = new Document();
        doc1.setId(DOC1_ID);
        doc1.setUploadId(UPLOAD1_ID);
        List<IDocument> result = new ArrayList<IDocument>();
        result.add(doc1);
        
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                return result;
            }
            
        };
        
        List<IDocument> searchResult = partiallyMockedClient.getDocumentByUploadId(UPLOAD1_ID);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertEquals(UPLOAD1_ID, searchResult.get(0).getUploadId());
    }
    
    @Test
    public void test_getDocumentByUploadId_severalDocExists() throws Exception {
        IDocument doc1 = new Document();
        doc1.setId(DOC1_ID);
        doc1.setUploadId(UPLOAD1_ID);
        
        IDocument doc2 = new Document();
        doc2.setId(DOC2_ID);
        doc2.setUploadId(UPLOAD1_ID);
        
        List<IDocument> result = new ArrayList<IDocument>();
        result.add(doc1);
        result.add(doc2);
        
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                return result;
            }
            
        };
        
        List<IDocument> searchResult = partiallyMockedClient.getDocumentByUploadId(UPLOAD1_ID);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.stream().allMatch(r -> r.getUploadId().equals(UPLOAD1_ID)));
    }
    
    @Test
    public void test_getDocumentByUploadId_docDoesNotExist() {
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                return new ArrayList<IDocument>();
            }
            
        };
        
        List<IDocument> searchResult = partiallyMockedClient.getDocumentByUploadId(UPLOAD1_ID);
        Assert.assertEquals(0, searchResult.size());
    }
    
    @Test
    public void test_getDocumentsByUsername_docExists() throws Exception {
        IDocument doc1 = new Document();
        doc1.setId(DOC1_ID);
        doc1.setUsername(USERNAME);
        
        List<IDocument> result = new ArrayList<IDocument>();
        result.add(doc1);
        
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                return result;
            }
            
        };
        
        List<IDocument> searchResult = partiallyMockedClient.getDocumentsByUsername(USERNAME);
        Assert.assertEquals(1, searchResult.size());
        Assert.assertEquals(USERNAME, searchResult.get(0).getUsername());
    }
    
    @Test
    public void test_getDocumentsByUsername_severalDocExists() throws Exception {
        IDocument doc1 = new Document();
        doc1.setId(DOC1_ID);
        doc1.setUsername(USERNAME);
        
        IDocument doc2 = new Document();
        doc2.setId(DOC2_ID);
        doc2.setUsername(USERNAME);
        
        List<IDocument> result = new ArrayList<IDocument>();
        result.add(doc1);
        result.add(doc2);
        
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                return result;
            }
            
        };
        
        List<IDocument> searchResult = partiallyMockedClient.getDocumentsByUsername(USERNAME);
        Assert.assertEquals(2, searchResult.size());
        Assert.assertTrue(searchResult.stream().allMatch(r -> r.getUsername().equals(USERNAME)));
    }
    
    @Test
    public void test_getDocumentsByUsername_docDoesNotExists() {
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                // TODO Auto-generated method stub
                return new ArrayList<IDocument>();
            }
            
        };
        
        List<IDocument> searchResult = partiallyMockedClient.getDocumentsByUsername(USERNAME);
        Assert.assertEquals(0, searchResult.size());
    }
    
    @Test
    public void test_getDocumentByRequestId_docExists() throws Exception {
        IDocument doc1 = new Document();
        doc1.setId(DOC1_ID);
        doc1.setRequestId(REQUEST_ID);
        
        List<IDocument> result = new ArrayList<IDocument>();
        result.add(doc1);
        
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                return result;
            }
            
        };
        
        IDocument searchResult = partiallyMockedClient.getDocumentByRequestId(REQUEST_ID);
        Assert.assertEquals(doc1, searchResult);
        Assert.assertEquals(REQUEST_ID, searchResult.getRequestId());
    }
    
    @Test
    public void test_getDocumentByRequestId_docDoesNotExists() throws Exception {
        // let's mock the superclass method
        IDocumentDatabaseClient partiallyMockedClient = new DocumentDatabaseClient() {

            @Override
            protected List<IDocument> searchByProperty(String propName, String propValue,
                    Class<? extends IDocument> clazz) {
                return new ArrayList<IDocument>();
            }
            
        };
        
        IDocument searchResult = partiallyMockedClient.getDocumentByRequestId(REQUEST_ID);
        Assert.assertEquals(null, searchResult);
    }
    
    class DocumentIdArgumentMatcher extends ArgumentMatcher<Document> {
        
        private String docId;
        
        public DocumentIdArgumentMatcher(String docId) {
            this.docId = docId;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument == null || !(argument instanceof Document)) {
                return false;
            }
            if (docId.equals(((IDocument)argument).getId())) {
                return true;
            }
            return false;
        }
        
    }
    
    class UploadIdArgumentMatcher extends ArgumentMatcher<Upload> {

        private String uploadId;
        
        public UploadIdArgumentMatcher(String uploadId) {
            this.uploadId = uploadId;
        }
        
        @Override
        public boolean matches(Object argument) {
            if (((IDocument)argument).getUploadId().equals(uploadId)) {
                return true;
            }
            return false;
        }
        
    }
}
