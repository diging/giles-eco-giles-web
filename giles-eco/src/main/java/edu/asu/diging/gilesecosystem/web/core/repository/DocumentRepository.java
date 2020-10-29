package edu.asu.diging.gilesecosystem.web.core.repository;

import org.springframework.data.repository.CrudRepository;

import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;

public interface DocumentRepository extends CrudRepository<Document, String> {

}
