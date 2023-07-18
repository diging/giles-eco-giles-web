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

import edu.asu.diging.gilesecosystem.requests.ICompletedImageExtractionRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.requests.impl.CompletedImageExtractionRequest;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.files.impl.ProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.repository.ProcessingRequestRepository;

public class ProcessingRequestsDatabaseClientTest {
    
    @Mock
    private ProcessingRequestRepository processingRequestRepository;

    @Mock
    private ISystemMessageHandler messageHandler;

    @InjectMocks
    private ProcessingRequestsDatabaseClient processingRequestsDatabaseClient;
    
    private IProcessingRequest processingRequest;
    
    private final String DOCUMENT_ID = "DOC123";
    private final String FILE_ID = "FILE123";
    private final String REQUEST_ID = "REQ123";
    private final String PROCESSING_REQUEST_ID = "PROC123";

    @Before
    public void setUp() {
        processingRequestsDatabaseClient = new ProcessingRequestsDatabaseClient(processingRequestRepository);
        MockitoAnnotations.initMocks(this);
        processingRequest = createProcessingRequest(DOCUMENT_ID, FILE_ID, REQUEST_ID, PROCESSING_REQUEST_ID);
    }
    
    @Test
    public void test_getRequestByDocumentId_success() {
        List<ProcessingRequest> processingRequests = new ArrayList();
        processingRequests.add((ProcessingRequest) processingRequest);
        Mockito.when(processingRequestRepository.findByDocumentId(Mockito.anyString())).thenReturn(processingRequests);
        Assert.assertEquals(processingRequests, processingRequestsDatabaseClient.getRequestByDocumentId(DOCUMENT_ID));
    }
    
    @Test
    public void test_getRequestByDocumentId_returnsEmptyList() {
        List<ProcessingRequest> processingRequests = new ArrayList();
        Mockito.when(processingRequestRepository.findByDocumentId(Mockito.anyString())).thenReturn(processingRequests);
        List<IProcessingRequest> res = processingRequestsDatabaseClient.getRequestByDocumentId(DOCUMENT_ID);
        Assert.assertEquals(processingRequests, res);
        Assert.assertEquals(0, res.size());
    }
    
    @Test
    public void test_getProcRequestsByRequestId_success() {
        List<ProcessingRequest> processingRequests = new ArrayList();
        processingRequests.add((ProcessingRequest) processingRequest);
        Mockito.when(processingRequestRepository.findByRequestId(Mockito.anyString())).thenReturn(processingRequests);
        Assert.assertEquals(processingRequests, processingRequestsDatabaseClient.getProcRequestsByRequestId(REQUEST_ID));
    }
    
    @Test
    public void test_getProcRequestsByRequestId_returnsEmptyList() {
        List<ProcessingRequest> processingRequests = new ArrayList();
        Mockito.when(processingRequestRepository.findByRequestId(Mockito.anyString())).thenReturn(processingRequests);
        List<IProcessingRequest> res = processingRequestsDatabaseClient.getRequestByDocumentId(DOCUMENT_ID);
        Assert.assertEquals(processingRequests, res);
        Assert.assertEquals(0, res.size());
    }
    
    @Test
    public void test_saveNewRequest_success() {
        Mockito.when(processingRequestRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        processingRequestsDatabaseClient.saveNewRequest(processingRequest);
        Mockito.verify(processingRequestRepository).save((ProcessingRequest) processingRequest);
    }
    
    @Test
    public void test_saveNewRequest_failure() {
        Mockito.when(processingRequestRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(processingRequestRepository.save(Mockito.any())).thenThrow(new IllegalArgumentException());
        processingRequestsDatabaseClient.saveNewRequest(processingRequest);
        Mockito.verify(messageHandler).handleMessage(Mockito.anyString(), Mockito.any(Exception.class), Mockito.any());
    }
    
    @Test
    public void test_saveRequest_success() {
        processingRequestsDatabaseClient.saveRequest(processingRequest);
        Mockito.verify(processingRequestRepository).save((ProcessingRequest) processingRequest);
    }
    
    @Test
    public void test_saveRequest_failure() {
        Mockito.when(processingRequestRepository.save(Mockito.any())).thenThrow(new IllegalArgumentException());
        processingRequestsDatabaseClient.saveRequest(processingRequest);
        Mockito.verify(messageHandler).handleMessage(Mockito.anyString(), Mockito.any(Exception.class), Mockito.any());
    }
    
    @Test
    public void test_getIncompleteRequests_success() {
        processingRequest.setCompletedRequest(null);
        List<ProcessingRequest> processingRequests = new ArrayList();
        processingRequests.add((ProcessingRequest) processingRequest);
        Mockito.when(processingRequestRepository.findByCompletedRequestIsNull()).thenReturn(processingRequests);
        Assert.assertEquals(processingRequests, processingRequestsDatabaseClient.getIncompleteRequests());
    }
    
    @Test
    public void test_getIncompleteRequests_returnsEmptyList() {
        List<ProcessingRequest> processingRequests = new ArrayList();
        Mockito.when(processingRequestRepository.findByCompletedRequestIsNull()).thenReturn(processingRequests);
        List<IProcessingRequest>res = processingRequestsDatabaseClient.getIncompleteRequests();
        Assert.assertEquals(processingRequests, res);
        Assert.assertEquals(0, res.size());
    }

    private IProcessingRequest createProcessingRequest(String documentId, String fileId, String requestId, String id) {
        ICompletedImageExtractionRequest completedRequest = new CompletedImageExtractionRequest();
        completedRequest.setDocumentId(documentId);
        completedRequest.setFileId(fileId);
        completedRequest.setId(1);
        completedRequest.setStatus(RequestStatus.COMPLETE);
        IProcessingRequest newProcessingRequest = new ProcessingRequest();
        newProcessingRequest.setDocumentId(documentId);
        newProcessingRequest.setFileId(fileId);
        newProcessingRequest.setRequestId(requestId);
        newProcessingRequest.setId(id);
        newProcessingRequest.setCompletedRequest(completedRequest);
        return newProcessingRequest;
    }
}
