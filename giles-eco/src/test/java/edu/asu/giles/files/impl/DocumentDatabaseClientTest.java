package edu.asu.giles.files.impl;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import edu.asu.giles.core.IDocument;
import edu.asu.giles.core.impl.Document;
import edu.asu.giles.core.impl.Upload;
import edu.asu.giles.db4o.impl.DatabaseManager;
import edu.asu.giles.exceptions.UnstorableObjectException;
import edu.asu.giles.files.IDocumentDatabaseClient;

public class DocumentDatabaseClientTest {
    
    Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    @Mock private ObjectContainer container;
    
    @Mock private DatabaseManager databaseManager;
    
    @Mock private ObjectSet<Object> objectSetForDoc1;
    
    @Mock private Iterator<Object> mockedIterator;
    
    @InjectMocks private IDocumentDatabaseClient docDatabaseClientToTest;
    
    private final String DOC1_ID = "DOC1";
    private final String DOC_ID_NOT_EXIST = "DOC_NOT_EXISTS";
    private final String UPLOAD1_ID = "UP1";
    
    @Before
    public void setUp() {
        docDatabaseClientToTest = new DocumentDatabaseClient();
        MockitoAnnotations.initMocks(this);
        
        IDocument doc1 = new Document();
        doc1.setId(DOC1_ID);
        doc1.setUploadId(UPLOAD1_ID);
        
        Mockito.when(objectSetForDoc1.size()).thenReturn(1);
        Mockito.when(objectSetForDoc1.get(0)).thenReturn(doc1);
        Mockito.when(objectSetForDoc1.iterator()).thenReturn(mockedIterator);
        Mockito.when(mockedIterator.hasNext()).thenReturn(true, false);
        Mockito.when(mockedIterator.next()).thenReturn(doc1);
        
    }
    
    @Test
    public void test_saveDocument() throws UnstorableObjectException {
        IDocument doc = new Document();
        doc.setId("id");
        docDatabaseClientToTest.saveDocument(doc);
        Mockito.verify(container).store(doc);
        Mockito.verify(container).commit();
    }
    
    @Test(expected = UnstorableObjectException.class)
    public void test_saveDocument_noId() throws UnstorableObjectException {
        IDocument doc = new Document();
        docDatabaseClientToTest.saveDocument(doc);
    }
    
    @Test
    public void test_getDocumentById_docExists() {
        Mockito.when(container.queryByExample(Mockito.argThat(new DocumentIdArgumentMatcher(DOC1_ID)))).thenReturn(objectSetForDoc1);
        IDocument doc = docDatabaseClientToTest.getDocumentById(DOC1_ID);
        Assert.assertEquals(DOC1_ID, doc.getId());
    }
    
    @Test
    public void test_getDocumentById_docNotExists() {
        Mockito.when(container.queryByExample(Mockito.argThat(new DocumentIdArgumentMatcher(DOC1_ID)))).thenReturn(objectSetForDoc1);
        IDocument doc = docDatabaseClientToTest.getDocumentById(DOC_ID_NOT_EXIST);
        Assert.assertNull(doc);
    }
    
    @Test
    public void test_getDocumentByUploadId_docExists() {
        Mockito.when(container.queryByExample(Mockito.argThat(new UploadIdArgumentMatcher(UPLOAD1_ID)))).thenReturn(objectSetForDoc1);
        List<IDocument> doc = docDatabaseClientToTest.getDocumentByUploadId(UPLOAD1_ID);
        Assert.assertEquals(1, doc.size());
        Assert.assertEquals(UPLOAD1_ID, doc.get(0).getUploadId());
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
