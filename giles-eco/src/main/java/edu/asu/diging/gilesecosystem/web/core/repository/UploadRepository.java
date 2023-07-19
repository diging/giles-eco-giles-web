package edu.asu.diging.gilesecosystem.web.core.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;

@Repository
public interface UploadRepository extends JpaRepository<Upload, String> {
    List<IUpload> findByUsername(String username);
    long countByUsername(String username);
    Upload findByUploadProgressId(String progressId);
    List<IUpload> findByUsername(String username, Sort sort);
    List<Upload> findByUsername(String username, Pageable pageable);
    List<Upload> findAllOrderBy(String sortBy, Pageable pageable);
}
