package edu.asu.diging.gilesecosystem.web.service.upload.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.service.upload.IUploadService;
import edu.asu.diging.gilesecosystem.web.users.User;
import edu.asu.diging.gilesecosystem.web.util.FileUploadHelper;
import edu.asu.diging.gilesecosystem.web.util.IStatusHelper;

@Service
public class UploadService implements IUploadService {
     
    @Autowired
    private FileUploadHelper uploadHelper;

    @Autowired
    private IPropertiesManager propertiesManager;
    
    @Autowired
    private IFilesManager filesManager;
    
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
    public String startUpload(DocumentAccess access, DocumentType type,
            MultipartFile[] files, List<byte[]> fileBytes, User user) {
        String uploadProgressId = generateId();
        uploadHelper.processUpload(access, type, files, fileBytes, user, uploadProgressId);
     
        return uploadProgressId;
    }

    @Override
    public List<StorageStatus> getUpload(String id) {
        List<StorageStatus> statuses = new ArrayList<>();
        IUpload upload = filesManager.getUploadByProgressId(id);
        if (upload != null) {
            final List<StorageStatus> stats = new ArrayList<>();
            for (IDocument doc : filesManager.getDocumentsByUploadId(upload.getId())) {
                String uploadedFileId = doc.getUploadedFileId();
                IFile uploadedFile = filesManager.getFile(uploadedFileId);
                stats.add(new StorageStatus(doc, uploadedFile, null, statusHelper.isProcessingDone(doc) ? RequestStatus.COMPLETE : RequestStatus.SUBMITTED));
            }
            statuses.addAll(stats);
        }
    
        return statuses;
    }

    protected String generateId() {
        String id = null;
        while (true) {
            id = "PROG" + generateUniqueId();
            IUpload existingUpload = filesManager.getUploadByProgressId(id);;
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
}
