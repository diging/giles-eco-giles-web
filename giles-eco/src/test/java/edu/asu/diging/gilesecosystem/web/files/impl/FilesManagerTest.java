package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.domain.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.domain.IDocument;
import edu.asu.diging.gilesecosystem.web.domain.IFile;
import edu.asu.diging.gilesecosystem.web.domain.IPage;
import edu.asu.diging.gilesecosystem.web.domain.impl.Document;
import edu.asu.diging.gilesecosystem.web.domain.impl.File;
import edu.asu.diging.gilesecosystem.web.domain.impl.Page;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.service.core.ITransactionalFileService;

public class FilesManagerTest {

    @Mock private EntityManager em;
    @Mock private ITransactionalDocumentService documentService;
    @Mock private ITransactionalFileService fileService;
    @InjectMocks private IFilesManager filesManagerToTest;

    private String DOCUMENT_ID = "documentId";
    private String IMG_ID = "imgId";
    private String TXT_ID = "txtId";
    private String OCR_ID = "ocrId";
    private IDocument doc = null;
    private IFile imgFile = null;
    private IFile txtFile = null;
    private IFile ocrFile = null;


    @Before
    public void setUp() throws UnstorableObjectException {
        filesManagerToTest = new FilesManager();
        MockitoAnnotations.initMocks(this);
        doc = new Document();
        doc.setId(DOCUMENT_ID);
        doc.setAccess(DocumentAccess.PRIVATE);

        imgFile = new File();
        imgFile.setId(IMG_ID);
        imgFile.setAccess(DocumentAccess.PRIVATE);

        txtFile = new File();
        txtFile.setId(TXT_ID);
        txtFile.setAccess(DocumentAccess.PRIVATE);

        ocrFile = new File();
        ocrFile.setId(OCR_ID);
        ocrFile.setAccess(DocumentAccess.PRIVATE);

        IPage page = new Page();
        page.setImageFileId("imgId");
        page.setTextFileId("txtId");
        page.setOcrFileId("ocrId");

        ArrayList<IPage> pages = new ArrayList<>();
        pages.add(page);
        doc.setPages(pages);

        Mockito.when(fileService.getFileById(IMG_ID)).thenReturn((File) imgFile);
        Mockito.when(fileService.getFileById(TXT_ID)).thenReturn((File) txtFile);
        Mockito.when(fileService.getFileById(OCR_ID)).thenReturn((File) ocrFile);
    }

    @Test
    public void test_changeDocumentAccess() throws UnstorableObjectException {
        boolean isChangeSuccess = filesManagerToTest.changeDocumentAccess(doc, DocumentAccess.PUBLIC);
        Mockito.verify(documentService).saveDocument(doc);
        Assert.assertEquals(isChangeSuccess, true);
        Assert.assertEquals(doc.getAccess(), DocumentAccess.PUBLIC);

        Mockito.verify(fileService).getFileById(IMG_ID);
        Assert.assertEquals(imgFile.getAccess(), DocumentAccess.PUBLIC);

        Mockito.verify(fileService).getFileById(TXT_ID);
        Assert.assertEquals(txtFile.getAccess(), DocumentAccess.PUBLIC);

        Mockito.verify(fileService).getFileById(OCR_ID);
        Assert.assertEquals(ocrFile.getAccess(), DocumentAccess.PUBLIC);
    }
}
