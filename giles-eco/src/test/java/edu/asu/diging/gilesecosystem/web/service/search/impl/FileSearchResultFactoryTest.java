package edu.asu.diging.gilesecosystem.web.service.search.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.googlecode.mp4parser.boxes.dece.AssetInformationBox;

import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.impl.File;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.rest.FilesController;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.service.search.FileSearchResult;

public class FileSearchResultFactoryTest {

    @Mock
    private IPropertiesManager propertiesManager;

    @Mock
    private IFilesManager filesManager;

    @InjectMocks
    private FileSearchResultFactory factoryToTest;

    private String FILE_ID = "fileId";
    private String CONTENT_TYPE = "contentType";
    private String DOCUMENT_ID = "documentId";
    private String FILENAME = "filename";
    private long SIZE = 1000;
    private String UPLOAD_DATE = "date";
    private String UPLOAD_ID = "uploadId";
    private DocumentAccess access = DocumentAccess.PRIVATE;

    private String GILES_URL = "gilesUrl";

    @Before
    public void setUp() {
        factoryToTest = new FileSearchResultFactory();
        MockitoAnnotations.initMocks(this);

        IFile file = new File();
        file.setId(FILE_ID);
        file.setAccess(access);
        file.setContentType(CONTENT_TYPE);
        file.setDocumentId(DOCUMENT_ID);
        file.setFilename(FILENAME);
        file.setSize(SIZE);
        file.setUploadDate(UPLOAD_DATE);
        file.setUploadId(UPLOAD_ID);

        Mockito.when(propertiesManager.getProperty(Properties.GILES_URL)).thenReturn(
                GILES_URL);
        Mockito.when(filesManager.getFile(FILE_ID)).thenReturn(file);
    }

    @Test
    public void test_createSearchResult_extractedTextFile() {
        IDocument document = new Document();
        document.setId(DOCUMENT_ID);
        document.setExtractedTextFileId(FILE_ID);

        Mockito.when(filesManager.getDocument(DOCUMENT_ID)).thenReturn(document);

        FileSearchResult result = factoryToTest.createSearchResult(FILE_ID);
        Assert.assertEquals(access, result.getAccess());
        Assert.assertEquals(FILE_ID, result.getId());
        Assert.assertEquals(CONTENT_TYPE, result.getContentType());
        Assert.assertEquals(DOCUMENT_ID, result.getDocumentId());
        Assert.assertEquals(FILENAME, result.getFilename());
        Assert.assertEquals(SIZE, result.getSize());
        Assert.assertEquals(UPLOAD_DATE, result.getUploadDate());
        Assert.assertEquals(UPLOAD_ID, result.getUploadId());
        Assert.assertEquals(
                GILES_URL
                        + FilesController.GET_DOCUMENT_PATH.replace(
                                FilesController.DOCUMENT_ID_PLACEHOLDER, DOCUMENT_ID),
                result.getDocumentUrl());
        Assert.assertEquals(
                GILES_URL
                        + FilesController.DOWNLOAD_FILE_URL.replace(
                                FilesController.FILE_ID_PLACEHOLDER, FILE_ID),
                result.getUrl());
    }
}
