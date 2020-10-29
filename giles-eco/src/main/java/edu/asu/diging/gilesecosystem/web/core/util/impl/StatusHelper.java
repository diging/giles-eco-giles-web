package edu.asu.diging.gilesecosystem.web.core.util.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.IRequest;
import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.files.IProcessingRequestsDatabaseClient;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.util.IStatusHelper;

@Service
public class StatusHelper implements IStatusHelper {

    @Autowired
    private IProcessingRequestsDatabaseClient procReqDbClient;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.gilesecosystem.web.util.impl.IStatusHelper#
     * isProcessingComplete(edu.asu.diging.gilesecosystem.web.core.IDocument)
     */
    @Override
    public boolean isProcessingDone(IDocument document) {
        List<IProcessingRequest> procRequests = procReqDbClient
                .getRequestByDocumentId(document.getId());
        long uncompletedRequests = procRequests
                .stream()
                .filter(req -> req.getRequestStatus() != RequestStatus.COMPLETE
                        && req.getRequestStatus() != RequestStatus.FAILED).count();
        if (uncompletedRequests > 0) {
            return false;
        }
        return true;
    }

    @Override
    public RequestStatus getFileProcessingResult(IFile file) {
        List<IProcessingRequest> procRequests = procReqDbClient
                .getRequestByDocumentId(file.getDocumentId());

        if (procRequests.stream().filter(preq -> preq.getFileId().equals(file.getId()))
                .anyMatch(preq -> preq.getRequestStatus() == RequestStatus.SUBMITTED)) {
            return RequestStatus.SUBMITTED;
        }
        if (procRequests.stream().filter(preq -> preq.getFileId().equals(file.getId()))
                .anyMatch(preq -> preq.getRequestStatus() == RequestStatus.NEW)) {
            return RequestStatus.NEW;
        }
        if (procRequests.stream().filter(preq -> preq.getFileId().equals(file.getId()))
                .allMatch(preq -> preq.getRequestStatus() == RequestStatus.COMPLETE)) {
            return RequestStatus.COMPLETE;
        }
        return RequestStatus.FAILED;
    }

    @Override
    public RequestStatus getProcessingPhaseResult(Class<? extends IRequest> requestClass,
            List<IProcessingRequest> procRequests) {
        if (procRequests
                .stream()
                .filter(preq -> requestClass.isAssignableFrom(preq.getSentRequest()
                        .getClass()))
                .anyMatch(preq -> preq.getRequestStatus() == RequestStatus.FAILED)) {
            return RequestStatus.FAILED;
        }

        if (procRequests
                .stream()
                .filter(preq -> requestClass.isAssignableFrom(preq.getSentRequest()
                        .getClass()))
                .anyMatch(preq -> preq.getRequestStatus() == RequestStatus.NEW)) {
            return RequestStatus.NEW;
        }

        if (procRequests
                .stream()
                .filter(preq -> requestClass.isAssignableFrom(preq.getSentRequest()
                        .getClass()))
                .anyMatch(preq -> preq.getRequestStatus() == RequestStatus.SUBMITTED)) {
            return RequestStatus.SUBMITTED;
        }

        return RequestStatus.COMPLETE;
    }
}
