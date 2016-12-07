package edu.asu.diging.gilesecosystem.web.files.impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.asu.diging.gilesecosystem.requests.RequestStatus;
import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;
import edu.asu.diging.gilesecosystem.util.properties.IPropertiesManager;
import edu.asu.diging.gilesecosystem.web.core.DocumentAccess;
import edu.asu.diging.gilesecosystem.web.core.DocumentType;
import edu.asu.diging.gilesecosystem.web.core.IDocument;
import edu.asu.diging.gilesecosystem.web.core.IFile;
import edu.asu.diging.gilesecosystem.web.core.IUpload;
import edu.asu.diging.gilesecosystem.web.core.ProcessingStatus;
import edu.asu.diging.gilesecosystem.web.core.impl.Document;
import edu.asu.diging.gilesecosystem.web.core.impl.Upload;
import edu.asu.diging.gilesecosystem.web.exceptions.GilesProcessingException;
import edu.asu.diging.gilesecosystem.web.files.IDocumentDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesDatabaseClient;
import edu.asu.diging.gilesecosystem.web.files.IFilesManager;
import edu.asu.diging.gilesecosystem.web.files.IUploadDatabaseClient;
import edu.asu.diging.gilesecosystem.web.service.IFileHandlerRegistry;
import edu.asu.diging.gilesecosystem.web.service.IFileTypeHandler;
import edu.asu.diging.gilesecosystem.web.service.processing.IProcessingCoordinator;
import edu.asu.diging.gilesecosystem.web.service.processing.impl.StorageRequestProcessingInfo;
import edu.asu.diging.gilesecosystem.web.service.properties.Properties;
import edu.asu.diging.gilesecosystem.web.users.User;

@Transactional
@PropertySource("classpath:/config.properties")
@Service
public class FilesManager implements IFilesManager {

    private Logger logger = LoggerFactory.getLogger(FilesManager.class);

    @Autowired
    private IPropertiesManager propertyManager;

    @Autowired
    private IFilesDatabaseClient databaseClient;

    @Autowired
    private IUploadDatabaseClient uploadDatabaseClient;

    @Autowired
    private IDocumentDatabaseClient documentDatabaseClient;

    @Autowired
    private IFileHandlerRegistry fileHandlerRegistry;
    
    @Autowired
    private IProcessingCoordinator processCoordinator;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.giles.files.impl.IFilesManager#addFiles(java.util.List)
     */
    @Override
    public List<StorageStatus> addFiles(Map<IFile, byte[]> files,
            User user, DocumentType docType, DocumentAccess access) {

        String username = user.getUsername();
        String uploadId = uploadDatabaseClient.generateId();
        String uploadDate = OffsetDateTime.now(ZoneId.of("UTC")).toString();

        IUpload upload = createUpload(username, uploadId, uploadDate);

        List<StorageStatus> statuses = new ArrayList<StorageStatus>();
        IDocument document = null;
        if (docType == DocumentType.MULTI_PAGE) {
            document = createDocument(uploadId, uploadDate, access, docType, username);
        }
        for (IFile file : files.keySet()) {
            if (docType == DocumentType.SINGLE_PAGE) {
                document = createDocument(uploadId, uploadDate,
                        file.getAccess(), docType, username);
            }

            byte[] content = files.get(file);
            if (content == null) {
                statuses.add(new StorageStatus(document, file, null,
                        RequestStatus.FAILED));
                continue;
            }

            String id = databaseClient.generateId();

            

            file.setId(id);
            file.setDocumentId(document.getId());
            file.setUploadId(uploadId);
            file.setUploadDate(uploadDate);
            file.setUsername(username);
            file.setFilepath(getRelativePathOfFile(file));
            file.setProcessingStatus(ProcessingStatus.UNPROCESSED);

            document.getFileIds().add(id);
            document.setUploadedFileId(file.getId());

            try {
                documentDatabaseClient.saveDocument(document);
                
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
                logger.error("Could not store uploaded files.", e);
                statuses.add(new StorageStatus(document, file, e, RequestStatus.FAILED));
            } catch (UnstorableObjectException e) {
                logger.error("Object is not storable. Please review your code.", e);
                statuses.add(new StorageStatus(document, file, new GilesProcessingException(e), RequestStatus.FAILED));
            } 
        }

        boolean atLeastOneSuccess = statuses.stream().anyMatch(
                status -> status.getStatus() != RequestStatus.FAILED);
        if (atLeastOneSuccess) {
            try {
                uploadDatabaseClient.saveUpload(upload);
            } catch (UnstorableObjectException e) {
                // let's silently fail because this should never happen
                // we set the id
                logger.error("Could not store upload.", e);
            }
        }

        return statuses;
    }

    private IUpload createUpload(String username, String uploadId,
            String uploadDate) {
        IUpload upload = new Upload(uploadId);
        upload.setCreatedDate(uploadDate);
        upload.setUsername(username);
        return upload;
    }

    private IDocument createDocument(String uploadId, String uploadDate,
            DocumentAccess access, DocumentType docType, String username) {
        IDocument document = new Document();
        String docId = documentDatabaseClient.generateId();
        document.setDocumentId(docId);
        document.setUsername(username);
        document.setId(docId);
        document.setCreatedDate(uploadDate);
        document.setAccess(access);
        document.setUploadId(uploadId);
        document.setFileIds(new ArrayList<>());
        document.setTextFileIds(new ArrayList<>());
        document.setDocumentType(docType);

        return document;
    }

    @Override
    public List<IFile> getFilesByUploadId(String uploadId) {
        return databaseClient.getFilesByUploadId(uploadId);
    }

    @Override
    public List<IDocument> getDocumentsByUploadId(String uploadId) {
        List<IDocument> documents = documentDatabaseClient
                .getDocumentByUploadId(uploadId);
        for (IDocument doc : documents) {
            doc.setFiles(new ArrayList<>());
            for (String fileId : doc.getFileIds()) {
                doc.getFiles().add(databaseClient.getFileById(fileId));
            }
        }
        return documents;
    }

    @Override
    public IFile getFile(String id) {
        if (id == null) {
            return null;
        }
        return databaseClient.getFileById(id);
    }

    @Override
    public IFile getFileByPath(String path) {
        IFile file = new edu.asu.diging.gilesecosystem.web.core.impl.File();
        file.setFilepath(path);

        List<IFile> files = databaseClient.getFilesByPath(path);
        if (files == null || files.isEmpty()) {
            return null;
        }

        return files.get(0);
    }
    
    @Override
    public IFile getFileByRequestId(String requestId) {
        return databaseClient.getFileByRequestId(requestId);
    }

    @Override
    public byte[] getFileContent(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getFileContent(file);
    }

    @Override
    public void saveFile(IFile file) throws UnstorableObjectException {
        databaseClient.saveFile(file);
    }

    @Override
    public List<IUpload> getUploadsOfUser(String username, int page, int pageSize, String sortBy, int sortDirection) {
        int defaultPageSize = new Integer(propertyManager.getProperty(Properties.DEFAULT_PAGE_SIZE));
        if (pageSize == -1) {
            pageSize = defaultPageSize;
        }
        if (page < 1) {
            page = 1;
        }
        int pageCount = getUploadsOfUserPageCount(username);
        pageCount = pageCount > 0 ? pageCount : 1;
        if (page > pageCount) {
            page = pageCount;
        }
        return uploadDatabaseClient.getUploadsForUser(username, page, pageSize, sortBy, sortDirection);
    }
    
    @Override
    public Map<String, Map<String, String>> getUploadedFilenames(String username) {
        List<IDocument> documents = documentDatabaseClient.getDocumentsByUsername(username);
        Map<String, Map<String, String>> filenames = new HashMap<String, Map<String, String>>();
        
        for (IDocument doc : documents) {
            Map<String, String> filenameList = filenames.get(doc.getUploadId());
            if (filenameList == null) {
                filenameList = new HashMap<String, String>();
                filenames.put(doc.getUploadId(), filenameList);
            }
            String fileId = doc.getUploadedFileId();
            IFile file = databaseClient.getFileById(fileId);
            filenameList.put(file.getId(), file.getFilename());
        }
        return filenames;
    }
    
    @Override
    public int getUploadsOfUserCount(String username) {
        List<IUpload> uploads = uploadDatabaseClient.getUploadsForUser(username);
        return uploads.size();
    }
    
    @Override
    public int getUploadsOfUserPageCount(String username) {
        int defaultPageSize = new Integer(propertyManager.getProperty(Properties.DEFAULT_PAGE_SIZE));
        int totalUploads = getUploadsOfUserCount(username);
        return (int) Math.ceil(new Double(totalUploads) / new Double(defaultPageSize));
    }

    @Override
    public IUpload getUpload(String id) {
        return uploadDatabaseClient.getUpload(id);
    }

    @Override
    public String getRelativePathOfFile(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getRelativePathOfFile(file);
    }

    @Override
    public String getFileUrl(IFile file) {
        IFileTypeHandler handler = fileHandlerRegistry.getHandler(file
                .getContentType());
        return handler.getFileUrl(file);
    }

    @Override
    public IDocument getDocument(String id) {
        return documentDatabaseClient.getDocumentById(id);
    }

    @Override
    public void saveDocument(IDocument document) throws UnstorableObjectException {
        documentDatabaseClient.saveDocument(document);
    }

    @Override
    public List<IFile> getFilesOfDocument(IDocument doc) {
        List<String> fileIds = doc.getFileIds();

        List<IFile> files = new ArrayList<>();
        fileIds.forEach(id -> files.add(getFile(id)));

        return files;
    }
    
    @Override
    public List<IFile> getTextFilesOfDocument(IDocument doc) {
        List<String> fileIds = doc.getTextFileIds();
        
        List<IFile> files = new ArrayList<>();
        fileIds.forEach(id -> files.add(getFile(id)));
        return files;
    }

}
