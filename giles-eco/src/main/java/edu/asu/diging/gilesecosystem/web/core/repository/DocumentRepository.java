package edu.asu.diging.gilesecosystem.web.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    List<IDocument> findByUploadId(String uploadId);

    List<IDocument> findByUsername(String username);
}
