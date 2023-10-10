package edu.asu.diging.gilesecosystem.web.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.requests.impl.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, String> {
    void deleteByDocumentId(String documentId);
}
