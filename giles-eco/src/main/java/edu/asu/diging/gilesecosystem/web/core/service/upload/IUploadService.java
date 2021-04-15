package edu.asu.diging.gilesecosystem.web.core.service.upload;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.gilesecosystem.web.core.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.users.User;

public interface IUploadService {

    public abstract String startUpload(DocumentAccess access, DocumentType type,
            MultipartFile[] files, List<byte[]> fileBytes, User user);

    public abstract List<StorageStatus> getUploadStatus(String id);

    IUpload getUpload(String id);

}