package edu.asu.giles.service.upload.impl;

import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.giles.core.DocumentAccess;
import edu.asu.giles.core.DocumentType;
import edu.asu.giles.files.impl.StorageStatus;
import edu.asu.giles.util.FileUploadHelper;

@Component
public class UploadThread {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private FileUploadHelper uploadHelper;
    
    @Async("uploadExecutor")
    public Future<List<StorageStatus>> runUpload(DocumentAccess access, DocumentType type, MultipartFile[] files, List<byte[]> fileBytes, String username) {
        logger.info("(" + Thread.currentThread().getId() + ") uploading files.");

        List<StorageStatus> statuses = uploadHelper.processUpload(access, type, files, fileBytes, username);
        return new AsyncResult<List<StorageStatus>>(statuses);
    }
}
