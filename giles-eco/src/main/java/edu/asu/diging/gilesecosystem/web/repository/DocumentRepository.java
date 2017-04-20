package edu.asu.diging.gilesecosystem.web.repository;

import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.gilesecosystem.web.domain.impl.Document;

public interface DocumentRepository extends CrudRepository<Document, String> {

}
