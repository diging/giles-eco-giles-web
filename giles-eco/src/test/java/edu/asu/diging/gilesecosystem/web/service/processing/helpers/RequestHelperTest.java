package edu.asu.diging.gilesecosystem.web.service.processing.helpers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import edu.asu.diging.gilesecosystem.requests.FileType;
import edu.asu.diging.gilesecosystem.requests.IRequestFactory;
import edu.asu.diging.gilesecosystem.requests.IStorageRequest;
import edu.asu.diging.gilesecosystem.requests.impl.StorageRequest;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;
import edu.asu.diging.gilesecosystem.web.core.service.processing.helpers.RequestHelper;

public class RequestHelperTest {
    
    @Mock
    private IRequestFactory<IStorageRequest, StorageRequest> requestFactory;
    
    @InjectMocks
    private RequestHelper requestHelper = new RequestHelper();
    
    private IStorageRequest storageRequest;
    private IFile file;
    
    @Before
    public void setUp() throws InstantiationException, IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        storageRequest = createStorageRequest();
        Mockito.when(requestFactory.createRequest(Mockito.anyString(), Mockito.anyString())).thenReturn(storageRequest);
        file = createFile();
    }
    
    @Test
    public void test_createStorageRequest_withImageExtracted_asFalse() throws GilesProcessingException {
        IStorageRequest request = requestHelper.createStorageRequest(file, "pdfs/github_37469232/UPzabVVEAlajNL/DOCuEO0La7renx9/HW1.jpg", "http://localhost:8082/nepomuk/rest/files/FILEl17JlMz3axqi", FileType.IMAGE, "STREQqyx8EJZwmqaE", false);
        Assert.assertEquals(request.isDerivedFile(), false);
    }
    
    @Test
    public void test_createStorageRequest_withImageExtracted_asTrue() throws GilesProcessingException {
        IStorageRequest request = requestHelper.createStorageRequest(file, "pdfs/github_37469232/UPzabVVEAlajNL/DOCuEO0La7renx9/HW1.jpg", "http://localhost:8082/nepomuk/rest/files/FILEl17JlMz3axqi", FileType.IMAGE, "STREQqyx8EJZwmqaE", true);
        Assert.assertEquals(request.isDerivedFile(), true);
    }
    
    @Test
    public void test_createStorageRequest_withPageNr() throws GilesProcessingException {
        IStorageRequest request = requestHelper.createStorageRequest(file, "pdfs/github_37469232/UPzabVVEAlajNL/DOCuEO0La7renx9/HW1.jpg", "http://localhost:8082/nepomuk/rest/files/FILEl17JlMz3axqi", FileType.IMAGE, "STREQqyx8EJZwmqaE", 1);
        Assert.assertEquals(request.getPageNr(), 1);
    }
    
    private IStorageRequest createStorageRequest() {
        storageRequest = new StorageRequest();
        storageRequest.setRequestId("REQ12345");
        storageRequest.setUploadId("UPLOAD12345");
        return storageRequest;
    }
    
    private IFile createFile() {
        file = new File();
        file.setDocumentId("DOC12345");
        file.setId("FILE12345");
        file.setUploadDate("2023-05-11T22:51:48.854995Z");
        file.setFilename("HW1.jpg");
        file.setUsernameForStorage("github_12345");
        return file;
    }
}
