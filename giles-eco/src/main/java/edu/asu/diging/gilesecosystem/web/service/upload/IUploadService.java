package edu.asu.diging.gilesecosystem.web.service.upload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.files.impl.StorageStatus;

public interface IUploadService {

    public abstract String startUpload(DocumentAccess access, DocumentType type,
            MultipartFile[] files, List<byte[]> fileBytes, String username);

    public abstract List<StorageStatus> getUpload(String id);

    public abstract long countNonExpiredUpload();


}