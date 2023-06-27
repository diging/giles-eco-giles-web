package edu.asu.diging.gilesecosystem.web.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.impl.Upload;

@Repository
public interface UploadRepository extends JpaRepository<IUpload, String>{
    List<IUpload> findByUsername(String username);
    long countByUsername(String username);
    Upload findByUploadProgressId(String progressId);
    List<IUpload> findByUsernameOrderBy(String username, String sortBy);
    List<IUpload> findAllOrderBy(String sortBy);
    List<IUpload> findByUsernameOrderBy(String username, String sortBy, int page, int pageSize);
    List<IUpload> findAllOrderBy(String sortBy, int page, int pageSize);
}
