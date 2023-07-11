package edu.asu.diging.gilesecosystem.web.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;

@Repository
public interface ProcessingRequestRepository extends JpaRepository<ProcessingRequest, String> {

    List<ProcessingRequest> findByDocumentId(String docId);

    List<ProcessingRequest> findByRequestId(String procReqId);

    List<ProcessingRequest> findByCompletedRequestIsNull();
}
