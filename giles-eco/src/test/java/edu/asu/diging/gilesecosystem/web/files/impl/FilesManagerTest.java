package edu.asu.diging.gilesecosystem.web.files.impl;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.impl.Document;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;

public class FilesManagerTest {

    @Mock private EntityManager em;
    @Mock private IDocumentDatabaseClient docDatabaseClientToTest;
    @InjectMocks private FilesManager filesManagerToTest;

    private String DOCUMENT_ID = "documentId";
    private DocumentAccess accessPrivate = DocumentAccess.PRIVATE;
    private DocumentAccess accessPublic = DocumentAccess.PUBLIC;
    IDocument doc = null;

    @Before
    public void setUp() throws UnstorableObjectException {
        filesManagerToTest = new FilesManager();
        MockitoAnnotations.initMocks(this);
        doc = new Document();
        doc.setId(DOCUMENT_ID);
        doc.setAccess(accessPrivate);
    }

    @Test
    public void test_changeDocumentAccess() throws UnstorableObjectException {
        boolean isChangeSuccess = filesManagerToTest.changeDocumentAccess(doc, accessPublic);
        Mockito.verify(docDatabaseClientToTest).saveDocument(doc);
        Assert.assertEquals(isChangeSuccess, true);
        Assert.assertEquals(doc.getAccess(), accessPublic);
    }
}
