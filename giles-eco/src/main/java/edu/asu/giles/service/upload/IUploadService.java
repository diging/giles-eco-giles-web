package edu.asu.giles.service.upload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.files.impl.StorageStatus;

public interface IUploadService {

    public abstract String startUpload(DocumentAccess access, DocumentType type,
            MultipartFile[] files, List<byte[]> fileBytes, String username);

    public abstract List<StorageStatus> getUpload(String id);

    public abstract long countNonExpiredUpload();


}