package edu.asu.diging.gilesecosystem.web.core.service.upload.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.upload.IUploadService;
import edu.asu.diging.gilesecosystem.web.core.users.User;
import edu.asu.diging.gilesecosystem.web.core.util.FileUploadHelper;
import edu.asu.diging.gilesecosystem.web.core.util.IStatusHelper;

@Service
public class UploadService implements IUploadService {
     
    @Autowired
    private FileUploadHelper uploadHelper;

    @Autowired
    private IFilesManager filesManager;
    
    @Autowired
    private ITransactionalFileService fileService;
    
    @Autowired
    private ITransactionalUploadService uploadService;
    
    @Autowired
    private IStatusHelper statusHelper;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.giles.service.upload.impl.IUploadService#startUpload(edu.asu.
     * giles.core.DocumentAccess, edu.asu.giles.core.DocumentType,
     * org.springframework.web.multipart.MultipartFile[], java.lang.String)
     */
    @Override
    public UploadIds startUpload(DocumentAccess access, DocumentType type,
            MultipartFile[] files, List<byte[]> fileBytes, User user) {
        String uploadProgressId = generateId();
        List<StorageStatus> statuses = uploadHelper.processUpload(access, type, files, fileBytes, user, uploadProgressId);
     
        UploadIds ids = new UploadIds(uploadProgressId, statuses.stream().map(s -> s.getDocument().getId()).collect(Collectors.toList()));
        
        return ids;
    }

    /**
     * Get a list of statuses for each file uploaded in an upload identified by its
     * progress id.
     * 
     * @param id Progress id of upload to check.
     */
    @Override
    public List<StorageStatus> getUploadStatus(String id) {
        List<StorageStatus> statuses = new ArrayList<>();
        IUpload upload = uploadService.getUploadByProgressId(id);
        if (upload != null) {
            final List<StorageStatus> stats = new ArrayList<>();
            for (IDocument doc : filesManager.getDocumentsByUploadId(upload.getId())) {
                String uploadedFileId = doc.getUploadedFileId();
                IFile uploadedFile = fileService.getFileById(uploadedFileId);
                stats.add(new StorageStatus(doc, uploadedFile, null, statusHelper.isProcessingDone(doc) ? RequestStatus.COMPLETE : RequestStatus.SUBMITTED));
            }
            statuses.addAll(stats);
        }
    
        return statuses;
    }
    
    /** 
     * Get an upload by its progress id.
     * 
     * @param id Progress id of upload.
     */
    @Override
    public IUpload getUpload(String id) {
        return uploadService.getUploadByProgressId(id);
    }

    protected String generateId() {
        String id = null;
        while (true) {
            id = "PROG" + generateUniqueId();
            IUpload existingUpload = uploadService.getUploadByProgressId(id);;
            if (existingUpload == null) {
                break;
            }
        }
        return id;
    }

    /**
     * This methods generates a new 6 character long id. Note that this method
     * does not assure that the id isn't in use yet.
     * 
     * Adapted from
     * http://stackoverflow.com/questions/9543715/generating-human-readable
     * -usable-short-but-unique-ids
     * 
     * @return 6 character id
     */
    protected String generateUniqueId() {
        char[] chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
                .toCharArray();

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(chars[random.nextInt(62)]);
        }

        return builder.toString();
    }
    
    public class UploadIds {
        
        public UploadIds(String progressId, List<String> uploadIds) {
            this.progressId = progressId;
            this.uploadIds = uploadIds;
        }
        public String progressId;
        public List<String> uploadIds;
    }
}
