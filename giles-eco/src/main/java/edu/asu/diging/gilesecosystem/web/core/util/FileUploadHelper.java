package edu.asu.diging.gilesecosystem.web.core.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.files.impl.StorageStatus;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.impl.File;
import edu.asu.diging.gilesecosystem.web.core.users.User;

@Service
public class FileUploadHelper {
    
    @Autowired
    private IFilesManager filesManager;

    @Autowired
    private ISystemMessageHandler messageHandler;
 
    public List<StorageStatus> processUpload(DocumentAccess access, DocumentType docType,
            MultipartFile[] files, List<byte[]> fileBytes, User user,
            String uploadProgressId) {
        Map<IFile, byte[]> uploadedFiles = new HashMap<>();
        
        if (access == null) {
            access = DocumentAccess.PRIVATE;
        }
        
        int i = 0;
        for (MultipartFile f : files) {
            IFile file = new File(f.getOriginalFilename());
           
            byte[] bytes = null;
            try {
                if(fileBytes != null && fileBytes.size() == files.length) {
                    bytes = fileBytes.get(i);
                } else {
                    bytes = f.getBytes();
                }
                i++;
                
                uploadedFiles.put(file, bytes);
            } catch (IOException e2) {
                messageHandler.handleMessage("Couldn't get file content.", e2, MessageType.ERROR);
                uploadedFiles.put(file, null);
            }
            
            String contentType = null;
            
            if (bytes != null) {
               Tika tika = new Tika();
               contentType = tika.detect(bytes);
            }
            
            if (contentType == null) {
                contentType = f.getContentType();
            }
            
            file.setContentType(contentType);
            file.setSize(f.getSize());
            file.setAccess(access);
        }

        return filesManager.addFiles(uploadedFiles, user, docType, access, uploadProgressId);
    }
}
