package edu.asu.diging.gilesecosystem.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.gilesecosystem.web.core.model.impl.ProcessingRequest;

public interface ProcessingRequestRepository extends CrudRepository<ProcessingRequest, String> {

}
