package edu.asu.diging.gilesecosystem.web.core.files.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.septemberutil.properties.MessageType;
import edu.asu.diging.gilesecosystem.septemberutil.service.ISystemMessageHandler;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.web.core.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.core.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.model.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.model.IDocument;
import edu.asu.diging.gilesecosystem.web.core.model.IFile;
import edu.asu.diging.gilesecosystem.web.core.model.IPage;
import edu.asu.diging.gilesecosystem.web.core.model.IUpload;
import edu.asu.diging.gilesecosystem.web.core.model.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.service.IFileHandlerRegistry;
import edu.asu.diging.gilesecosystem.web.core.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalDocumentService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalFileService;
import edu.asu.diging.gilesecosystem.web.core.service.core.ITransactionalUploadService;
import edu.asu.diging.gilesecosystem.web.core.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.core.service.processing.impl.StorageRequestProcessingInfo;
import edu.asu.diging.gilesecosystem.web.core.users.User;

@PropertySource("classpath:/config.properties")
@Service
public class FilesManager implements IFilesManager {

    @Autowired
    private ITransactionalFileService fileService;
    
    @Autowired
    private ITransactionalDocumentService documentService;
    
    @Autowired
    private ITransactionalUploadService uploadService;
    
    @Autowired
    private IFileHandlerRegistry fileHandlerRegistry;
    
    @Autowired
    private IProcessingCoordinator processCoordinator;

    @Autowired
    private ISystemMessageHandler messageHandler;
    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.impl.IFilesManager#addFiles(java.util.List)
     */
    @Override
    public List<StorageStatus> addFiles(Map<IFile, byte[]> files,
            User user, DocumentType docType, DocumentAccess access, String uploadProgressId) {

        String username = user.getUsername();
        String uploadId = uploadService.generateUploadId();
        String uploadDate = OffsetDateTime.now(ZoneId.of("UTC")).toString();

        IUpload upload = uploadService.createUpload(username, user.getProvider(), uploadId, uploadDate, uploadProgressId);

        List<StorageStatus> statuses = new ArrayList<StorageStatus>();
        IDocument document = null;
        if (docType == DocumentType.MULTI_PAGE) {
            document = documentService.createDocument(uploadId, uploadDate, access, docType, username);
        }
        for (IFile file : files.keySet()) {
            if (docType == DocumentType.SINGLE_PAGE) {
                document = documentService.createDocument(uploadId, uploadDate,
                        file.getAccess(), docType, username);
            }

            byte[] content = files.get(file);
            if (content == null) {
                statuses.add(new StorageStatus(document, file, null,
                        RequestStatus.FAILED));
                continue;
            }

            String id = fileService.generateFileId();

            file.setId(id);
            file.setDocumentId(document.getId());
            file.setUploadId(uploadId);
            file.setUploadDate(uploadDate);
            file.setUsername(username);
            file.setProcessingStatus(ProcessingStatus.UNPROCESSED);

            document.getFileIds().add(id);
            document.setUploadedFileId(file.getId());

            try {
                documentService.saveDocument(document);
                
                // send file off to be stored
                StorageRequestProcessingInfo info = new StorageRequestProcessingInfo();
                info.setContent(content);
                info.setDocument(document);
                info.setFile(file);
                info.setProvider(user.getProvider());
                info.setProviderUsername(user.getUserIdOfProvider());
                info.setUpload(upload);
                RequestStatus requestStatus = processCoordinator.processFile(file, info);
                statuses.add(new StorageStatus(document, file, null, requestStatus));
            } catch (GilesProcessingException e) {
                messageHandler.handleMessage("Could not store uploaded files.", e, MessageType.ERROR);
                statuses.add(new StorageStatus(document, file, e, RequestStatus.FAILED));
            } catch (UnstorableObjectException e) {
                messageHandler.handleMessage("Object is not storable. Please review your code.", e, MessageType.ERROR);
                statuses.add(new StorageStatus(document, file, new GilesProcessingException(e), RequestStatus.FAILED));
            } 
        }

        boolean atLeastOneSuccess = statuses.stream().anyMatch(
                status -> status.getStatus() != RequestStatus.FAILED);
        if (atLeastOneSuccess) {
            try {
                uploadService.saveUpload(upload);
            } catch (UnstorableObjectException e) {
                // let's silently fail because this should never happen
                // we set the id
                messageHandler.handleMessage("Could not store upload.", e, MessageType.ERROR);
            }
        }

        return statuses;
    }

    @Override
    public List<IDocument> getDocumentsByUploadId(String uploadId) {
        List<IDocument> documents = documentService
                .getDocumentsByUploadId(uploadId);
        for (IDocument doc : documents) {
            doc.setFiles(new ArrayList<>());
            for (String fileId : doc.getFileIds()) {
                doc.getFiles().add(fileService.getFileById(fileId));
            }
        }
        return documents;
    }

    @Override
    public byte[] getFileContent(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getFileContent(file);
    }

    public Map<String, Map<String, String>> getUploadedFilenames(String username) {
        List<IDocument> documents = documentService.getDocumentsByUsername(username);
        Map<String, Map<String, String>> filenames = new HashMap<String, Map<String, String>>();
        
        for (IDocument doc : documents) {
            Map<String, String> filenameList = filenames.get(doc.getUploadId());
            if (filenameList == null) {
                filenameList = new HashMap<String, String>();
                filenames.put(doc.getUploadId(), filenameList);
            }
            String fileId = doc.getUploadedFileId();
            IFile file = fileService.getFileById(fileId);
            filenameList.put(file.getId(), file.getFilename());
        }
        return filenames;
    }

    
    @Override
    public String getFileUrl(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getFileUrl(file);
    }

    @Override
    public List<IFile> getFilesOfDocument(IDocument doc) {
        List<String> fileIds = doc.getFileIds();

        List<IFile> files = new ArrayList<>();
        fileIds.forEach(id -> files.add(fileService.getFileById(id)));

        return files;
    }
    
    @Override
    public List<IFile> getTextFilesOfDocument(IDocument doc) {
        List<String> fileIds = doc.getTextFileIds();
        
        List<IFile> files = new ArrayList<>();
        fileIds.forEach(id -> files.add(fileService.getFileById(id)));
        return files;
    }

    @Override
    public boolean changeDocumentAccess(IDocument doc, DocumentAccess docAccess) throws UnstorableObjectException {

        doc.setAccess(docAccess);
        documentService.saveDocument(doc);

        boolean isChangeSuccess = true;

        for (IPage page : doc.getPages()) {
            IFile imgFile = fileService.getFileById(page.getImageFileId());
            if (imgFile != null) {
                isChangeSuccess = isChangeSuccess && changeFileAccess(imgFile, docAccess);
            }

            IFile txtFile = fileService.getFileById(page.getTextFileId());
            if (txtFile != null) {
                isChangeSuccess = isChangeSuccess && changeFileAccess(txtFile, docAccess);
            }

            IFile ocrFile = fileService.getFileById(page.getOcrFileId());
            if (ocrFile != null) {
                isChangeSuccess = isChangeSuccess && changeFileAccess(ocrFile, docAccess);
            }
        }

        return isChangeSuccess;
    }

    private boolean changeFileAccess(IFile file, DocumentAccess docAccess) {
        file.setAccess(docAccess);
        try {
            fileService.saveFile(file);
        } catch (UnstorableObjectException e) {
            messageHandler.handleMessage("Could not store file.", e, MessageType.ERROR);
            return false;
        }
        return true;
    }

    @Override
    public void deleteFile(String fileId) {
        fileService.deleteFile(fileId);   
    } 
}
