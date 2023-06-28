package edu.asu.diging.gilesecosystem.web.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.web.core.model.IProcessingRequest;
import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;

@Repository
public interface ProcessingRequestRepository extends JpaRepository<ProcessingRequest, String> {

    List<IProcessingRequest> findByDocumentId(String docId);

    List<IProcessingRequest> findByRequestId(String procReqId);

    List<IProcessingRequest> findByCompletedRequestIsNull();
}
