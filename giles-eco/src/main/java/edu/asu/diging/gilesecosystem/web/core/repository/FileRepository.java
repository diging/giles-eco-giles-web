package edu.asu.diging.gilesecosystem.web.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.asu.diging.gilesecosystem.web.core.model.IFile;

@Repository
public interface FileRepository extends JpaRepository<IFile, String>{

    List<IFile> findByUploadId(String uploadId);

    List<IFile> findByUsername(String username);

    List<IFile> findByFilepath(String path);

    IFile findByFilename(String filename);

    IFile findByRequestId(String requestId);

    List<IFile> findByDerivedFrom(String derivedFromId);
}
