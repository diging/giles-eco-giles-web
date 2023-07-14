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

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.files.impl.FilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;
import edu.asu.diging.gilesecosystem.web.core.repository.FileRepository;

public class FilesDatabaseClientTest {
    @Mock
    private FileRepository fileRepository;
    
    @InjectMocks
    private FilesDatabaseClient filesDatabaseClient;
    
    IFile file1;

    @Before
    public void setUp() {
        filesDatabaseClient = new FilesDatabaseClient(fileRepository);
        MockitoAnnotations.initMocks(this);
        file1 = createFile("FILE123", "testFile", "testUser", "test/filepath", "UPL123", "FILE0123");
        Mockito.when(fileRepository.findById(Mockito.anyString())).thenReturn(Optional.of((File) file1));
    }
    
    @Test
    public void test_saveFile_success() throws IllegalArgumentException, UnstorableObjectException {
        filesDatabaseClient.saveFile(file1);
        Mockito.verify(fileRepository).save((File) file1);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void test_saveFile_throwsIllegalArgumentException() throws IllegalArgumentException, UnstorableObjectException {
        Mockito.when(fileRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(fileRepository.save(Mockito.any())).thenThrow(new IllegalArgumentException());
        filesDatabaseClient.saveFile(file1);
    }
    
    @Test(expected=UnstorableObjectException.class)
    public void test_saveFile_throwsUnstorableObjectException() throws IllegalArgumentException, UnstorableObjectException {
        IFile file2 = new File();
        Mockito.when(fileRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        filesDatabaseClient.saveFile(file2);
    }
    
    @Test
    public void test_getFileById_success() {
        IFile res = filesDatabaseClient.getFileById("FILE123");
        Assert.assertEquals(file1, res);
    }
    
    @Test
    public void test_getFileById_returnsNull() {
        Mockito.when(fileRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        IFile res = filesDatabaseClient.getFileById("FILE123");
        Assert.assertNull(res);
    }
    
    @Test
    public void test_getFilesForIds_success() {
        IFile file2 = createFile("FILE234", "testFile2", "testUser", "test/filepath2", "UPL234", "FILE123");
        List<String> ids = new ArrayList();
        ids.add("FILE123");
        ids.add("FILE234");
        List<File> files = new ArrayList();
        files.add((File) file1);
        files.add((File) file2);
        Mockito.when(fileRepository.findByIdIn(ids)).thenReturn(files);
        List<IFile> resFiles = filesDatabaseClient.getFilesForIds(ids);
        Assert.assertEquals(files, resFiles);
    }
    
    @Test
    public void test_getFilesForIds_returnsEmptyList() {
        IFile file2 = createFile("FILE234", "testFile2", "testUser", "test/filepath2", "UPL234", "FILE123");
        List<String> ids = new ArrayList();
        ids.add("FILE123");
        ids.add("FILE234");
        Mockito.when(fileRepository.findByIdIn(ids)).thenReturn(new ArrayList());
        List<IFile> resFiles = filesDatabaseClient.getFilesForIds(ids);
        Assert.assertEquals(0, resFiles.size());
    }
    
    @Test
    public void test_getFilesByUploadId_success() {
        IFile file2 = createFile("FILE234", "testFile2", "testUser", "test/filepath2", "UPL234", "FILE123");
        List<IFile> files = new ArrayList();
        files.add(file1);
        files.add(file2);
        Mockito.when(fileRepository.findByUploadId(Mockito.anyString())).thenReturn(files);
        List<IFile> resFiles = filesDatabaseClient.getFilesByUploadId("UPL123");
        Assert.assertEquals(files, resFiles);
    }
    
    @Test
    public void test_getFilesByUploadId_returnsEmptyList() {
        Mockito.when(fileRepository.findByUploadId(Mockito.anyString())).thenReturn(new ArrayList());
        List<IFile> resFiles = filesDatabaseClient.getFilesByUploadId("UPL123");
        Assert.assertEquals(0, resFiles.size());
    }
    
    @Test
    public void test_getFilesByUsername_success() {
        IFile file2 = createFile("FILE234", "testFile2", "testUser", "test/filepath2", "UPL234", "FILE123");
        List<IFile> files = new ArrayList();
        files.add(file1);
        files.add(file2);
        Mockito.when(fileRepository.findByUsername("testUser")).thenReturn(files);
        List<IFile> resFiles = filesDatabaseClient.getFilesByUsername("testUser");
        Assert.assertEquals(files, resFiles);
    }
    
    @Test
    public void test_getFilesByUsername_returnsEmptyList() {
        Mockito.when(fileRepository.findByUploadId(Mockito.anyString())).thenReturn(new ArrayList());
        List<IFile> resFiles = filesDatabaseClient.getFilesByUsername("testUser");
        Assert.assertEquals(0, resFiles.size());
    }
    
    @Test
    public void test_getFilesByPath_success() {
        List<IFile> files = new ArrayList();
        files.add(file1);
        Mockito.when(fileRepository.findByFilepath("test/filepath")).thenReturn(files);
        List<IFile> resFiles = filesDatabaseClient.getFilesByPath("test/filepath");
        Assert.assertEquals(files, resFiles);
    }
    
    @Test
    public void test_getFilesByPath_returnEmptyList() {
        Mockito.when(fileRepository.findByFilepath("test/filepath")).thenReturn(new ArrayList());
        List<IFile> resFiles = filesDatabaseClient.getFilesByPath("test/filepath");
        Assert.assertEquals(0, resFiles.size());
    }
    
    @Test
    public void test_getFilesByDerivedFrom_success() {
        List<IFile> files = new ArrayList();
        files.add(file1);
        Mockito.when(fileRepository.findByDerivedFrom("FILE123")).thenReturn(files);
        List<IFile> resFiles = filesDatabaseClient.getFilesByDerivedFrom("FILE123");
        Assert.assertEquals(files, resFiles);
    }
    
    @Test
    public void test_getFilesByDerivedFrom_returnsEmptyList() {
        Mockito.when(fileRepository.findByFilepath("test/filepath")).thenReturn(new ArrayList());
        List<IFile> resFiles = filesDatabaseClient.getFilesByDerivedFrom("FILE123");
        Assert.assertEquals(0, resFiles.size());
    }
    
    @Test
    public void test_getFile_success() {
        Mockito.when(fileRepository.findByFilename(Mockito.anyString())).thenReturn(file1);
        IFile res = filesDatabaseClient.getFile("FILE123");
        Assert.assertEquals(file1, res);
    }
    
    @Test
    public void test_getFile_returnsNull() {
        Mockito.when(fileRepository.findByFilename(Mockito.anyString())).thenReturn(null);
        IFile res = filesDatabaseClient.getFile("FILE123");
        Assert.assertNull(res);
    }
    
    @Test
    public void test_getFileByRequestId_success() {
        Mockito.when(fileRepository.findByRequestId(Mockito.anyString())).thenReturn(file1);
        IFile res = filesDatabaseClient.getFileByRequestId("REQ123");
        Assert.assertEquals(file1, res);
    }
    
    @Test
    public void test_getFileByRequestId_returnsNull() {
        Mockito.when(fileRepository.findByFilename(Mockito.anyString())).thenReturn(null);
        IFile res = filesDatabaseClient.getFileByRequestId("FILE123");
        Assert.assertNull(res);
    }
    
    private IFile createFile(String id, String testFile, String testUser, String filePath, String uploadId, String derivedFrom) {
        IFile file = new File();
        file.setId(id);
        file.setFilename(testFile);
        file.setUsername(testUser);
        file.setFilepath(filePath);
        file.setUploadId(uploadId);
        file.setDerivedFrom(derivedFrom);
        file.setRequestId("REQ123");
        return file;
    }
}
