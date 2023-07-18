package edu.asu.diging.gilesecosystem.web.files.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.files.impl.UploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;
import edu.asu.diging.gilesecosystem.web.core.repository.UploadRepository;

public class UploadDatabaseClientTest {
    
    @Mock
    private UploadRepository uploadRepository;

    @InjectMocks
    private UploadDatabaseClient uploadDatabaseClient;
    
    IUpload upload1;
    private final String UPLOAD_ID = "UPL123";
    private final String USERNAME = "testUser";
    private final String PROGRESS_ID = "PROG123";
    
    @Before
    public void setUp() {
        uploadDatabaseClient = new UploadDatabaseClient(uploadRepository);
        MockitoAnnotations.initMocks(this);
        upload1 = createUpload(UPLOAD_ID, USERNAME, PROGRESS_ID);
        Mockito.when(uploadRepository.findById(UPLOAD_ID)).thenReturn(Optional.of((Upload) upload1));
    }
    
    @Test
    public void test_saveUpload_success() throws IllegalArgumentException, UnstorableObjectException {
        Mockito.when(uploadRepository.findById(UPLOAD_ID)).thenReturn(Optional.empty());
        uploadDatabaseClient.saveUpload(upload1);
        Mockito.verify(uploadRepository).save((Upload) upload1);
    }
    
    @Test
    public void test_saveUpload_updateExistingUpload() throws IllegalArgumentException, UnstorableObjectException {
        uploadDatabaseClient.saveUpload(upload1);
        Mockito.verify(uploadRepository).save((Upload) upload1);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_saveUpload_throwsIllegalArgumentException() throws IllegalArgumentException, UnstorableObjectException {
        Mockito.when(uploadRepository.save((Upload) upload1)).thenThrow(new IllegalArgumentException());
        uploadDatabaseClient.saveUpload(upload1);
    }
    
    @Test(expected=UnstorableObjectException.class)
    public void test_saveUpload_throwsUnstorableObjectException() throws IllegalArgumentException, UnstorableObjectException {
        Mockito.when(uploadRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        IUpload upload2 = new Upload();
        uploadDatabaseClient.saveUpload(upload2);
    }
    
    @Test
    public void test_getUpload_success() {
        Assert.assertEquals(uploadDatabaseClient.getUpload(UPLOAD_ID), upload1);
    }
    
    @Test
    public void test_getUpload_returnsNull() {
        Mockito.when(uploadRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        Assert.assertNull(uploadDatabaseClient.getUpload(UPLOAD_ID));
    }
    
    @Test
    public void test_getUploadsForUser_success() {
        List<IUpload> uploads = new ArrayList();
        uploads.add(upload1);
        Mockito.when(uploadRepository.findByUsername(Mockito.anyString())).thenReturn(uploads);
        Assert.assertEquals(1, uploadDatabaseClient.getUploadsForUser(USERNAME).size());
    }
    
    @Test
    public void test_getUploadsForUser_returnsEmptyList() {
        List<IUpload> uploads = new ArrayList();
        Mockito.when(uploadRepository.findByUsername(Mockito.anyString())).thenReturn(uploads);
        Assert.assertEquals(0, uploadDatabaseClient.getUploadsForUser(USERNAME).size());
    }
    
    @Test
    public void test_getUploads_success() {
        List<Upload> uploads = new ArrayList();
        uploads.add((Upload) upload1);
        Mockito.when(uploadRepository.findAll()).thenReturn(uploads);
        List<IUpload> resUploads = new ArrayList();
        resUploads.add(upload1);
        Assert.assertEquals(resUploads, uploadDatabaseClient.getUploads());
    }
    
    @Test
    public void test_getUploadCountForUser_success() {
        Mockito.when(uploadRepository.countByUsername(Mockito.anyString())).thenReturn(1L);
        Assert.assertEquals(1L, uploadDatabaseClient.getUploadCountForUser(USERNAME));
    }
    
    @Test
    public void test_getUploadCount_success() {
        Mockito.when(uploadRepository.count()).thenReturn(1L);
        Assert.assertEquals(1L, uploadDatabaseClient.getUploadCount());
    }
    
    @Test
    public void test_getUploadsByProgressId_success() {
        Mockito.when(uploadRepository.findByUploadProgressId(Mockito.anyString())).thenReturn((Upload) upload1);
        Assert.assertEquals(upload1, uploadDatabaseClient.getUploadsByProgressId(PROGRESS_ID));
    }
    
    @Test
    public void test_getUploadsForUserWithPage_success() {
        List<Upload> uploads = new ArrayList<>();
        uploads.add((Upload) upload1);
        Mockito.when(uploadRepository.findByUsername(Mockito.anyString(), Mockito.any(PageRequest.class))).thenReturn(uploads);
        List<IUpload> expectedUploads = new ArrayList<>();
        expectedUploads.add(upload1);
        Assert.assertEquals(expectedUploads, uploadDatabaseClient.getUploadsForUser(USERNAME, 1, 10, "createdDate", new Integer(IUploadDatabaseClient.DESCENDING)));
    }
    
    @Test
    public void test_getUploadsWithPage_success() {
        List<Upload> uploads = new ArrayList<>();
        uploads.add((Upload) upload1);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "uploadId"));
        Page<Upload> pageResult = new PageImpl<>(uploads, pageRequest, uploads.size());
        Mockito.when(uploadRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(pageResult);
        List<IUpload> expectedUploads = new ArrayList<>();
        expectedUploads.add(upload1);
        Assert.assertEquals(expectedUploads, uploadDatabaseClient.getUploads(1, 10, "createdDate", new Integer(IUploadDatabaseClient.DESCENDING)));
    }
    
    private IUpload createUpload(String uploadId, String userName, String progressId) {
        IUpload upload = new Upload();
        upload.setId(uploadId);
        upload.setUsername(userName);
        upload.setUploadProgressId(progressId);
        return upload;
    }
}
